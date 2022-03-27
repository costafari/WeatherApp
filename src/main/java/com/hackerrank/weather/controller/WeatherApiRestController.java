package com.hackerrank.weather.controller;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import com.hackerrank.weather.model.Weather;
import com.hackerrank.weather.repository.WeatherRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
@Transactional
public class WeatherApiRestController {

    private final WeatherRepository weatherRepository;

    @Value("Application")
    private String applicationName;

    public WeatherApiRestController(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }


    @PostMapping("/weather")
    public ResponseEntity<?> createWeather(@Valid @RequestBody Weather wheather, BindingResult result) throws URISyntaxException {
        
        Weather w = null;
        Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			List<String> errores = result.getFieldErrors().stream()
					.map(err -> "The field ".concat(err.getField()).concat(" ").concat(err.getDefaultMessage()))
					.collect(Collectors.toList());
			response.put("errores", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			w = this.weatherRepository.save(wheather);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error INSERT in the database.");
			response.put("error", e.getMostSpecificCause().getMessage().concat(" -> ").concat(e.getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "The Weather had been inserted correctly.");
		response.put("weather", w);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
        
    }


	@GetMapping("/weather/{id}")
	public ResponseEntity<?> showById(@PathVariable Integer id) {

		Optional<Weather> w = null;
		Map<String, Object> response = new HashMap<>();

		try {
			w = this.weatherRepository.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error INSERT in the database.");
			response.put("error", e.getMostSpecificCause().getMessage().concat(" -> ").concat(e.getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (w == null) {
			response.put("mensaje",
					"The weather with ID: ".concat(id.toString()).concat(" has not been found in the database."));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Weather>(HttpStatus.OK);

	}


    @RequestMapping("/weather")
	public ResponseEntity<?> showByOptions(@RequestParam Map<String, List<String>> options) {

		Optional<Weather> w = null;
        Weather oneWeather = null;
		Map<String, Object> response = new HashMap<>();

		try {
			
            if(options.containsKey("date")) {
                w = this.weatherRepository.findByDate(options.get("date").get(0));
            }
            
            if(options.containsKey("city")) {
                w = this.weatherRepository.findByCity(options.get("city").get(0));
            }
            
            if(options.containsKey("sort")) {
                if(options.get("sort").get(0).equals("date")) {
                    w = this.weatherRepository.findByDateOrderByAsc(options.get("date").get(0));
                }
                else if(options.get("sort").get(0).equals("-date")) {
                    w = this.weatherRepository.findByDateOrderByDesc(options.get("date").get(0));
                }
            }

            if(options.isEmpty()) {
                oneWeather = (Weather) this.weatherRepository.findAll();
            }

		} catch (DataAccessException e) {
			response.put("mensaje", "Error SELECT in the database.");
			response.put("error", e.getMostSpecificCause().getMessage().concat(" -> ").concat(e.getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (w == null && oneWeather == null) {
			response.put("mensaje", "There are not Weathers in the database.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Weather>(HttpStatus.OK);

	}

}

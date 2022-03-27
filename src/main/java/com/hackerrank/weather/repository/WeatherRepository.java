package com.hackerrank.weather.repository;

import java.util.Optional;

import com.hackerrank.weather.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<Weather, Integer> {

    Optional<Weather> findByIdOrderByIdAsc(Integer id);

    Optional<Weather> findByDate(String string);

    Optional<Weather> findByCity(String string);

    Optional<Weather> findByDateOrderByAsc(String string);

    Optional<Weather> findByDateOrderByDesc(String string);
}

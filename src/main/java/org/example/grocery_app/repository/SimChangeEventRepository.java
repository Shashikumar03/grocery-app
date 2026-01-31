package org.example.grocery_app.repository;


//import com.shashi03.Bazzario.entity.SimChangeEvent;
//import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimChangeEventRepository extends JpaRepository<SimChangeEvent, Long> {

    List<SimChangeEvent> findByUserIdOrderByDetectedAtDesc(Long userId);
}

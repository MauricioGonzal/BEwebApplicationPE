package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.AttendanceRequest;
import com.aplicaciongimnasio.PuraEsencia.model.Attendance;
import com.aplicaciongimnasio.PuraEsencia.model.AttendanceType;
import com.aplicaciongimnasio.PuraEsencia.repository.AttendanceRepository;
import com.aplicaciongimnasio.PuraEsencia.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @PostMapping
    public ResponseEntity<String> markAttendance(@RequestBody AttendanceRequest request) {
        Long userId =  request.getUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().body("Falta userId");
        }
        String responseMessage = attendanceService.registerAttendance(request);
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Attendance>> getUserAttendance(@PathVariable Long userId) {
        List<Attendance> attendances = attendanceService.getAttendanceByUser(userId);
        return ResponseEntity.ok(attendances);
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<?> checkAttendanceStatus(@PathVariable Long userId) {
        LocalDate today = LocalDate.now();
        boolean isPresent = attendanceRepository.existsByUserIdAndDate(userId, today);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isPresent", isPresent);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/today")
    public ResponseEntity<List<Attendance>> getTodayAttendance() {
        LocalDate today = LocalDate.now();
        List<Attendance> presentUser = attendanceRepository.findByDate(today);
        return ResponseEntity.ok(presentUser);
    }

    @GetMapping("/{userId}/current-month")
    public ResponseEntity<Long> getAttendancesForUser(@PathVariable Long userId) {
        long count = attendanceService.getAttendancesInCurrentMonth(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{userId}/details")
    public ResponseEntity<List<Attendance>> getAttendanceDetails(@PathVariable Long userId) {
        List<Attendance> attendances = attendanceService.getAttendancesForCurrentMonth(userId);
        return ResponseEntity.ok(attendances);
    }
}
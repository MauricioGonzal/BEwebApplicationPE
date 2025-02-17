package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.Attendance;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.repository.AttendanceRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    public String registerAttendance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now(); // 🕒 Se registra la hora actual

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(today);
        attendance.setTime(now);
        attendanceRepository.save(attendance);

        return "Asistencia registrada: " + today + " a las " + now;
    }

    public List<Attendance> getAttendanceByUser(Long userId) {
        return attendanceRepository.findByUserIdAndDate(userId, LocalDate.now());
    }
}

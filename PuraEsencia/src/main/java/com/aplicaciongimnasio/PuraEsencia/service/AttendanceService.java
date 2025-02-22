package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.Attendance;
import com.aplicaciongimnasio.PuraEsencia.model.AttendanceType;
import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.repository.AttendanceRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.AttendanceTypeRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.PaymentRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import com.aplicaciongimnasio.PuraEsencia.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AttendanceTypeRepository attendanceTypeRepository;


    public String registerAttendance(Long userId, AttendanceType attendanceType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(attendanceType == null){
            Role role = user.getRole();
            if (role == Role.valueOf("CLIENT_GYM")) {
                attendanceType = attendanceTypeRepository.getById(1L);
            }
            else if(role == Role.valueOf("CLIENT_CLASSES")){
                attendanceType = attendanceTypeRepository.getById(2L);
            }
        }


        Optional<Payment> lastPayment = paymentRepository.findFirstByUserIdOrderByDueDateDesc(user.getId());

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now(); // 🕒 Se registra la hora actual

        if (lastPayment.isPresent()) {
            LocalDate dueDate = lastPayment.get().getDueDate();
            if (dueDate.isBefore(today)) {
                if(!isOutOfDueDate(user.getId())){
                    paymentService.registerPayment(user.getId(), 0f, "PENDIENTE", dueDate);
                }
            }
        }

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(today);
        attendance.setTime(now);
        attendance.setAttendanceType(attendanceType);
        attendanceRepository.save(attendance);

        return "Asistencia registrada: " + today + " a las " + now;
    }

    public List<Attendance> getAttendanceByUser(Long userId) {
        return attendanceRepository.findByUserIdAndDate(userId, LocalDate.now());
    }

    public Attendance getLastAttendanceByUser(Long userId){
        return attendanceRepository.findFirstByUserId(userId);
    }

    public Boolean isOutOfDueDate(Long userId){
        var isOutOfDueDate = false;
        Optional<Payment> lastPayment = paymentRepository.findFirstByUserIdOrderByDueDateDesc(userId);

        LocalDate today = LocalDate.now();

        if (lastPayment.isPresent()) {
            LocalDate dueDate = lastPayment.get().getDueDate();

            if (dueDate.isBefore(today)) {
                var limitDayToAssist = dueDate.plusDays(7);
                var lastAttendance = getLastAttendanceByUser(userId);
                if(lastAttendance == null || (lastAttendance.getDate().isAfter(dueDate) && lastAttendance.getDate().isBefore(limitDayToAssist))){
                    isOutOfDueDate = true;
                }
            }
        }
        return isOutOfDueDate;
    }
}

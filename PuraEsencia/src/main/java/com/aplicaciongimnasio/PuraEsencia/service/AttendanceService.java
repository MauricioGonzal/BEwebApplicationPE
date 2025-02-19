package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.Attendance;
import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.repository.AttendanceRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.PaymentRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
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



    public String registerAttendance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Optional<Payment> lastPayment = paymentRepository.findFirstByStatusAndUserIdOrderByDueDateDesc("PAGADO", user.getId());

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now(); // 🕒 Se registra la hora actual

        if (lastPayment.isPresent()) {
            LocalDate dueDate = lastPayment.get().getDueDate();

            if (dueDate.isBefore(today)) {
                paymentService.registerPayment(user.getId(), 0f, "PENDIENTE", dueDate);
                System.out.println("El último pago registrado tiene una fecha vencida: " + dueDate);
            } else {
                System.out.println("El último pago registrado no está vencido.");
            }
        }

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

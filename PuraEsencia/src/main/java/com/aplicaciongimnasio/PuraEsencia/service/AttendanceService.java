package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.AttendanceRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


    public String registerAttendance(AttendanceRequest attendanceRequest) {
        User user = userRepository.findById(attendanceRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long attendanceTypeId = attendanceRequest.getAttendanceTypeId();

        AttendanceType attendanceType = attendanceTypeRepository.findById(attendanceTypeId)
                .orElseThrow(() -> new RuntimeException("Tipo de asistencia no encontrado"));

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
                    paymentService.registerPayment(user.getId(), 0f, "PENDIENTE", dueDate, dueDate.plusMonths(1));
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

    public long getAttendancesInCurrentMonth(Long userId) {
        return attendanceRepository.countAttendancesInCurrentMonth(userId);
    }

    public Map<Long, Map<String, Integer>> getAttendancesForAllUsersInCurrentMonth() {
        // Fecha de inicio y fin del mes actual
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        // Obtener todas las asistencias en el rango de fechas para todos los usuarios
        List<Attendance> attendances = attendanceRepository.findByDateBetween(startOfMonth, endOfMonth);

        // Crear un mapa para almacenar las asistencias agrupadas por usuario y fecha
        Map<Long, Map<String, Integer>> result = new HashMap<>();

        // Procesar cada asistencia y agruparlas
        for (Attendance attendance : attendances) {
            Long userId = attendance.getUser().getId();
            String date = attendance.getDate().toString(); // Formatear la fecha a string

            // Si el usuario no está en el mapa, inicializamos su grupo de asistencias
            result.putIfAbsent(userId, new HashMap<>());

            // Obtener el mapa de asistencias para este usuario
            Map<String, Integer> userAttendances = result.get(userId);

            // Contar las asistencias por fecha
            userAttendances.put(date, userAttendances.getOrDefault(date, 0) + 1);
        }

        return result;
    }

    public List<Attendance> getAttendancesForCurrentMonth(Long userId) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return attendanceRepository.findByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);
    }
}

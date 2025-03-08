package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.AttendanceRequest;
import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.*;
import com.aplicaciongimnasio.PuraEsencia.model.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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

    @Autowired
    private MembershipRepository membershipRepository;


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
                    Membership membershipGym = membershipRepository.findByName("Mes Completo")
                            .orElseThrow(() -> new RuntimeException("Membresía 'Mes Completo' no encontrada"));
                    if(Objects.equals(attendanceType.getName(), "Gimnasio")){
                        paymentService.registerPayment(user.getId(), 0f, "PENDIENTE", dueDate, dueDate.plusMonths(1), membershipGym, null);
                    }
                    else{
                        paymentService.registerPayment(user.getId(), 0f, "PENDIENTE", dueDate, dueDate.plusMonths(1), null, null);
                    }
                }
            }
            else{
                if(Objects.equals(lastPayment.get().getStatus(), "PENDIENTE") && !Objects.equals(attendanceType.getName(), "Gimnasio")){
                    List<Attendance> attendancesDue = attendanceRepository.findByUserIdAndDateBetween(user.getId(), lastPayment.get().getPaymentDate(), dueDate);
                    Membership membership = lastPayment.get().getMembership();
                    if(membership.getMaxClasses() != null){
                        if(attendancesDue.size() > membership.getMaxClasses()){
                            Membership membershipNew = membershipRepository.findClosestMembership(membership.getTransactionCategory(), attendancesDue.size()).orElseThrow(() -> new RuntimeException("Tipo de membresia no encontrada"));
                            lastPayment.get().setMembership(membershipNew);
                            paymentRepository.save(lastPayment.get());
                        }
                    }
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
                if(lastAttendance != null && (lastAttendance.getDate().isAfter(dueDate) && lastAttendance.getDate().isBefore(limitDayToAssist))){
                    isOutOfDueDate = true;
                }
            }
        }
        else{
            isOutOfDueDate=true;
        }
        return isOutOfDueDate;
    }

    public long getAttendancesInCurrentMonth(Long userId) {
        return attendanceRepository.countAttendancesInCurrentMonth(userId);
    }

    public Map<Long, Map<String, Integer>> getAttendancesForAllUsersInCurrentMonth() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<Attendance> attendances = attendanceRepository.findByDateBetween(startOfMonth, endOfMonth);

        Map<Long, Map<String, Integer>> result = new HashMap<>();

        for (Attendance attendance : attendances) {
            Long userId = attendance.getUser().getId();
            String date = attendance.getDate().toString(); // Formatear la fecha a string

            result.putIfAbsent(userId, new HashMap<>());

            Map<String, Integer> userAttendances = result.get(userId);

            userAttendances.put(date, userAttendances.getOrDefault(date, 0) + 1);
        }

        return result;
    }

    public Map<Long, Map<String, Object>> getAttendancesForAllUsersInCurrentPaymentPeriod() {
        List<Payment> activePayments = paymentRepository.findLatestActivePayments(LocalDate.now());

        Map<Long, Map<String, Object>> result = new HashMap<>();

        for (Payment payment : activePayments) {
            Long userId = payment.getUser().getId();
            LocalDate startDate = payment.getPaymentDate();
            LocalDate endDate = payment.getDueDate();
            int maxClasses = 0;
            if(payment.getMembership() != null && payment.getMembership().getMaxClasses() != null){
                maxClasses = payment.getMembership().getMaxClasses(); // Obtener el máximo de clases
            }

            List<Attendance> attendances = attendanceRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

            Map<String, Integer> userAttendances = new HashMap<>();
            for (Attendance attendance : attendances) {
                String date = attendance.getDate().toString();
                userAttendances.put(date, userAttendances.getOrDefault(date, 0) + 1);
            }

            Map<String, Object> userData = new HashMap<>();
            userData.put("attendance", userAttendances);
            userData.put("max_classes", maxClasses);

            result.put(userId, userData);
        }

        return result;
    }

    public Integer getLeftAttendances(Long userId) {
        Payment activePayment = paymentRepository.findLatestActivePaymentsByUser(LocalDate.now(), userId);

        if(activePayment != null){
            LocalDate startDate = activePayment.getPaymentDate();
            LocalDate endDate = activePayment.getDueDate();
            int maxClasses = 0;
            if(activePayment.getMembership().getMaxClasses() != null){
                maxClasses = activePayment.getMembership().getMaxClasses(); // Obtener el máximo de clases
            }

            List<Attendance> attendances = attendanceRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

            return maxClasses - attendances.size();
        }

        return 0;

    }

    public List<Attendance> getAttendancesForCurrentMonth(Long userId) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return attendanceRepository.findByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);
    }
}

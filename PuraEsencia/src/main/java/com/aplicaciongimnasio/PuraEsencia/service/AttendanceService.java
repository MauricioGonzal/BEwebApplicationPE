package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.AttendanceRequest;
import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.*;
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

    @Autowired
    private MembershipItemRepository membershipItemRepository;

    @Autowired
    private ClassTypeRepository classTypeRepository;

    @Autowired
    private AreaRepository areaRepository;



    public String registerAttendance(AttendanceRequest attendanceRequest) {
        User user = userRepository.findById(attendanceRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Area area = areaRepository.findByName("Musculacion");
        List<AttendanceType> attendanceTypeList = attendanceTypeRepository.findByArea(area);

        ClassType classType = null;
        if(attendanceRequest.getClassTypeId() != null){
            area = areaRepository.findByName("Clases");
            attendanceTypeList = attendanceTypeRepository.findByArea(area);
            classType = classTypeRepository.findById(attendanceRequest.getClassTypeId())
                    .orElseThrow(() -> new RuntimeException("Clase no encontrado"));
        }

        Optional<Payment> lastPayment = paymentRepository.findFirstByUserIdOrderByDueDateDesc(user.getId());

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now(); // 🕒 Se registra la hora actual

        if (lastPayment.isPresent()) {
            LocalDate dueDate = lastPayment.get().getDueDate();
            if (dueDate.isBefore(today)) {
                if(!isOutOfDueDate(user.getId())){
                    paymentService.registerPayment(user.getId(), 0f, "PENDIENTE", dueDate, dueDate.plusMonths(1), null, null);
                }
            }
        }
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(today);
        attendance.setTime(now);
        attendance.setAttendanceType(attendanceTypeList.getFirst());
        attendance.setClassType(classType);
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
                if(lastAttendance == null || ((lastAttendance.getDate().isAfter(dueDate) && lastAttendance.getDate().isBefore(limitDayToAssist)))){
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

            // Calcular maxClasses
            int maxClasses = 0;
            if (payment.getMembership() != null) {
                Membership membership = payment.getMembership();
                if (membership.getMaxClasses() != null) {
                    maxClasses = membership.getMaxClasses();
                } else if ("Combinada".equals(membership.getMembershipType().getName())) {
                    List<MembershipItem> membershipItemList = membershipItemRepository.findByMembershipPrincipal(membership);
                    for (MembershipItem item : membershipItemList) {
                        Integer associatedMax = item.getMembershipAssociated().getMaxClasses();
                        if (associatedMax != null) {
                            maxClasses += associatedMax;
                        }
                    }
                }
            }

            // Obtener asistencias de este payment
            List<Attendance> attendances = attendanceRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

            // Si el usuario ya existe en el resultado, acumulamos
            if (result.containsKey(userId)) {
                Map<String, Object> userData = result.get(userId);

                // Acumular max_classes
                int previousMax = (int) userData.get("max_classes");
                userData.put("max_classes", previousMax + maxClasses);

                // Acumular asistencias sin perder fechas anteriores
                Map<String, Integer> attendanceMap = (Map<String, Integer>) userData.get("attendance");
                for (Attendance att : attendances) {
                    String date = att.getDate().toString();
                    attendanceMap.put(date, attendanceMap.getOrDefault(date, 0) + 1);
                }

            } else {
                // Nuevo usuario
                Map<String, Object> userData = new HashMap<>();
                Map<String, Integer> attendanceMap = new HashMap<>();
                for (Attendance att : attendances) {
                    String date = att.getDate().toString();
                    attendanceMap.put(date, attendanceMap.getOrDefault(date, 0) + 1);
                }
                userData.put("attendance", attendanceMap);
                userData.put("max_classes", maxClasses);
                result.put(userId, userData);
            }
        }

        return result;
    }


    public Integer getLeftAttendances(Long userId) {
        List<Payment> activeClassesPayment = paymentRepository.findActiveClassesPayment(LocalDate.now(), userId);
        Payment activeClassesCombinatedPayment = paymentRepository.findActiveClassesCombinatedPayment(LocalDate.now(), userId);
        List<Attendance> attendances = new ArrayList<>();
        int maxClasses = 0;
        Set<Long> attendanceIds = new HashSet<>();

        if(activeClassesCombinatedPayment != null){
            LocalDate startDate = activeClassesCombinatedPayment.getPaymentDate();
            LocalDate endDate = activeClassesCombinatedPayment.getDueDate();
            Membership membership = activeClassesCombinatedPayment.getMembership();
            if(membership.getMaxClasses() != null){
                maxClasses = activeClassesCombinatedPayment.getMembership().getMaxClasses(); // Obtener el máximo de clases
            }
            else if(Objects.equals(membership.getMembershipType().getName(), "Combinada")){
                List<MembershipItem> membershipItemList = membershipItemRepository.findByMembershipPrincipal(membership);
                for (MembershipItem membershipItem : membershipItemList){
                    if(membershipItem.getMembershipAssociated().getMaxClasses() != null) maxClasses += membershipItem.getMembershipAssociated().getMaxClasses();
                }

            }
            List<Attendance> newAttendances = attendanceRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
            for (Attendance attendance : newAttendances) {
                if (attendanceIds.add(attendance.getId())) {
                    attendances.add(attendance);
                }
            }

        }
        else{
            for(Payment payment : activeClassesPayment){
                LocalDate startDate = payment.getPaymentDate();
                LocalDate endDate = payment.getDueDate();
                Membership membership = payment.getMembership();
                if(membership.getMaxClasses() != null){
                    maxClasses += payment.getMembership().getMaxClasses(); // Obtener el máximo de clases
                }
                List<Attendance> newAttendances = attendanceRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
                for (Attendance attendance : newAttendances) {
                    if (attendanceIds.add(attendance.getId())) {
                        attendances.add(attendance);
                    }
                }
            }
        }

        return maxClasses - attendances.size();

    }

    public List<Attendance> getAttendancesForCurrentMonth(Long userId) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return attendanceRepository.findByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);
    }
}

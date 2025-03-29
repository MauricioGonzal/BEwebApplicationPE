package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.ClassSessionRequest;
import com.aplicaciongimnasio.PuraEsencia.model.ClassSchedule;
import com.aplicaciongimnasio.PuraEsencia.model.ClassSession;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.repository.ClassScheduleRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.ClassSessionRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassScheduleService {

    @Autowired
    private ClassScheduleRepository scheduleRepository;
    @Autowired
    private ClassSessionRepository sessionRepository;
    @Autowired
    private UserRepository userRepository;

    // Crear una nueva grilla semanal
    public ClassSchedule createSchedule(ClassSchedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public ClassSchedule createScheduleByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("ERROR!. Contactar con soporte."));
        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setGym(user.getGym());
        return scheduleRepository.save(classSchedule);
    }

    // Obtener todas las grillas de horarios
    public List<ClassSchedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    // Obtener una grilla por ID
    public Optional<ClassSchedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    public ClassSchedule getByAdmin(Long adminId){
        User user = userRepository.findById(adminId).orElseThrow(() -> new RuntimeException("ERROR!. Contactar con soporte."));

        List<ClassSchedule> classSchedules = scheduleRepository.getByAdmin(user);
        if(classSchedules.size() > 1) throw new RuntimeException("Error. Existen mas de una grilla para el gimnasio registrado.");
        else if(classSchedules.isEmpty()) return null;
        return classSchedules.getFirst();
    }

    // Crear una nueva sesión de clase dentro de un horario
    @Transactional
    public ClassSession createSession(Long scheduleId, ClassSessionRequest classSessionRequest) {
        ClassSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        /*ClassSchedule classSchedule = new ClassSchedule();

        scheduleRepository.save(classSchedule);*/
        ClassSession classSession = new ClassSession();
        classSession.setSchedule(schedule);
        classSession.setClassType(classSessionRequest.getClassType());
        classSession.setEndTime(classSessionRequest.getEndTime());
        classSession.setStartTime(classSessionRequest.getStartTime());
        classSession.setDayOfWeek(classSessionRequest.getDayOfWeek());
        classSession.setTeacher(classSessionRequest.getTeacher());
        return sessionRepository.save(classSession);
    }

    // Obtener todas las sesiones de clases
    public List<ClassSession> getAllSessions() {
        return sessionRepository.findAll();
    }

    // Obtener las sesiones de una grilla específica
    public List<ClassSession> getSessionsBySchedule(Long scheduleId) {
        return sessionRepository.findByScheduleId(scheduleId);
    }

}

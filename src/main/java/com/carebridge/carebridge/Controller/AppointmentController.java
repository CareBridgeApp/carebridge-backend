package com.carebridge.carebridge.Controller;

import com.carebridge.carebridge.Exception.SlotAlreadyBookedException;
import com.carebridge.carebridge.Service.AppointmentService;
import com.carebridge.carebridge.Dto.AppointmentRequestDTO;
import com.carebridge.carebridge.entity.Appointments;
import com.carebridge.carebridge.entity.UserDetails;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RequestMapping("/appointment")
@RestController
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;
    @PostMapping("/create")
    public ResponseEntity<?>createAppointment(@RequestBody AppointmentRequestDTO request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            Appointments appointment = appointmentService.addAppointment(id, request);
            return new ResponseEntity<>(appointment, HttpStatus.OK);
        }catch (SlotAlreadyBookedException e){
            return new ResponseEntity<>(Map.of("message",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/get/doctor")
    public ResponseEntity<?> getAppointmentsfordoctor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            List<Appointments> appointments= appointmentService.getappointmentsfordoctor(id);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>
                    (Map.of("message","Error While Getting Appointments"),HttpStatus.BAD_REQUEST);
        }
    }
    @PatchMapping("/{appointmentId}/confirm")
    public ResponseEntity<?>confirmAppointment(@PathVariable ObjectId appointmentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            Appointments appointments=appointmentService.confirmAppointment(id,appointmentId);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(Map.of("message",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/get/patient")
    public ResponseEntity<?> getAppointmentsforpatient() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            List<Appointments>appointments=appointmentService.getappointmentsforpatient(id);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("message","Error While getting appointments"),HttpStatus.BAD_REQUEST);
        }
    }
    @PatchMapping("/{appointmentId}/cancel")
    public ResponseEntity<?>cancelAppointment(@PathVariable ObjectId appointmentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            Appointments appointment=appointmentService.cancelAppointment(id,appointmentId);
            return new ResponseEntity<>(appointment, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return  new ResponseEntity<>(Map.of("message",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<LocalTime>> getBookedSlots(
            @PathVariable ObjectId doctorId,

            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        List<LocalTime> bookedSlots =
                appointmentService.getBookedSlots(
                        doctorId,
                        date
                );

        return ResponseEntity.ok(bookedSlots);
    }

}

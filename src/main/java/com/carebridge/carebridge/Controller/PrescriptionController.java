package com.carebridge.carebridge.Controller;

import com.carebridge.carebridge.Dto.CreatePrescriptionRequest;
import com.carebridge.carebridge.Repository.PrescriptionRepo;
import com.carebridge.carebridge.Service.PrescriptionService;
import com.carebridge.carebridge.entity.Prescription;
import com.carebridge.carebridge.entity.UserDetails;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.carebridge.carebridge.Dto.prescriptionResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prescription")
public class PrescriptionController {
    @Autowired
    private PrescriptionRepo prescriptionRepo;
    @Autowired
    private PrescriptionService prescriptionService;
    @PostMapping("/create")
    public ResponseEntity<?> createPrescription(@RequestBody CreatePrescriptionRequest request) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId userId = userDetails.getId();
            prescriptionResponse response =prescriptionService.createPrescription(request,userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch(Exception ex){
            return new ResponseEntity<>(Map.of("message",ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/medicalRecord/{medicalRecordId}")
    public ResponseEntity<?> updatePrescription(@RequestBody CreatePrescriptionRequest request){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId userId = userDetails.getId();
            prescriptionResponse response=prescriptionService.updatePrescription(request,userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch(Exception ex){
            return new ResponseEntity<>(Map.of("message",ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/medicalRecord/{medicalRecordId}")
    public ResponseEntity<?> getPrescriptionByMedicalRecord(@PathVariable("medicalRecordId") String medicalRecordId){
        try{
            ObjectId recordId=new ObjectId(medicalRecordId);
            prescriptionResponse response=prescriptionService.getPrescriptionByMedicalRecordId(recordId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch(Exception ex){
            return new ResponseEntity<>(Map.of("message",ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/myprescriptions")
    public ResponseEntity<?> getMyPrescriptions(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId userId = userDetails.getId();
            List<prescriptionResponse> response=prescriptionService.getMyPrescriptions(userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch(Exception ex){
            return new ResponseEntity<>(Map.of("message",ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @PatchMapping("/patientsPrescriptions/{PatientId}")
    public ResponseEntity<?> getPatientPrescriptions(@PathVariable("PatientId") String patient_Id){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId userId = userDetails.getId();
            ObjectId patientId=new ObjectId(patient_Id);
            List<prescriptionResponse>response=prescriptionService.getPrescriptionByPatientId(patientId,userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch(Exception ex){
            return new ResponseEntity<>(Map.of("message",ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/medicalRecord/{medicalRecordId}")
    public ResponseEntity<?> deletePrescription(@PathVariable("medicalRecordId") String medicalRecordId){
        try {
            Prescription prescription = prescriptionRepo.findById(new ObjectId(medicalRecordId)).get();
            prescriptionRepo.delete(prescription);
            return new ResponseEntity<>(Map.of("message", "Successfully deleted!"), HttpStatus.OK);
        }catch(Exception ex){
            return new ResponseEntity<>(Map.of("message","Error while deleting this prescription,please try later!"), HttpStatus.BAD_REQUEST);
        }
    }
}

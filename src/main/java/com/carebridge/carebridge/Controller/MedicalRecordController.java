package com.carebridge.carebridge.Controller;

import com.carebridge.carebridge.Dto.MedicalRecordRequest;
import com.carebridge.carebridge.Dto.medicalRecordResponse;
import com.carebridge.carebridge.Service.MedicalRecordService;
import com.carebridge.carebridge.entity.MedicalRecord;
import com.carebridge.carebridge.entity.UserDetails;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medical-records")
public class MedicalRecordController {
    @Autowired
    private MedicalRecordService medicalRecordService;
    @PostMapping("/create")
    public ResponseEntity<?> addMedicalRecord(@RequestBody MedicalRecordRequest request){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            MedicalRecord medicalRecord=medicalRecordService.createMedicalRecord(request,id);
            return new ResponseEntity<>(medicalRecord, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(Map.of("message",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/myRecords")
    public ResponseEntity<?> getMedicalRecords(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            List<MedicalRecord>records=medicalRecordService.getMyMedicalRecords(id);
            return new ResponseEntity<>(records, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(Map.of("message","Error while retrieving medical records"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("doctor/getAllRecords/{patientId}")
    public ResponseEntity<?> getAllPatientRecords(@PathVariable("patientId") String patientId){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            ObjectId PatientId=new ObjectId(patientId);
            List<MedicalRecord>records=medicalRecordService.getAllPatientsMedicalRecords(PatientId,id);
            return new ResponseEntity<>(records,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(Map.of("message",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("doctor/getRecords/{patientId}")
    public ResponseEntity<?> getPatientRecords(@PathVariable("patientId") String patientId){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            ObjectId PatientId=new ObjectId(patientId);
            List<MedicalRecord>records=medicalRecordService.getMedicalRecords(PatientId,id);
            return new ResponseEntity<>(records,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(Map.of("message",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("doctor/update/{medicalRecordId}")
    public ResponseEntity<?>updateMedicalRecord(@RequestBody MedicalRecordRequest request
            ,@PathVariable("medicalRecordId") String medicalRecordId){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            ObjectId MedicalRecordId=new ObjectId(medicalRecordId);
            medicalRecordResponse response=medicalRecordService.updateMedicalRecord(request,id,MedicalRecordId);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(Map.of("message",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

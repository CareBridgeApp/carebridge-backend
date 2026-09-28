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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.carebridge.carebridge.Dto.prescriptionResponse;

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

}

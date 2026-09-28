package com.carebridge.carebridge.Controller;

import com.carebridge.carebridge.Service.DoctorPatientService;
import com.carebridge.carebridge.entity.UserDetails;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/relationship")
public class DoctorPatientController {
    @Autowired
    private DoctorPatientService doctorPatientService;
    @PatchMapping("/{relationshipId}/approve")
    public ResponseEntity<?> approveRelationship(@PathVariable("relationshipId") String relationship_Id){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            ObjectId relationshipId = new ObjectId(relationship_Id);
            doctorPatientService.approveRelationship(relationshipId,id);
            return new ResponseEntity<>(Map.of("message","Access granted!"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(Map.of("message",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @PatchMapping("/{relationshipId}/revoke")
    public ResponseEntity<?> revokeRelationship(@PathVariable("relationshipId") ObjectId relationshipId){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ObjectId id = userDetails.getId();
            doctorPatientService.revokeRelationship(id, relationshipId);
            return new ResponseEntity<>(Map.of("message","Medical history access revoked successfully!"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(Map.of("message",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}

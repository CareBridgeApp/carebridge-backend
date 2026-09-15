package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Dto.DoctorRequestDTO;
import com.carebridge.carebridge.Dto.DoctorResponseDTO;
import com.carebridge.carebridge.entity.DoctorDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class DoctorSearchService {
    @Autowired
    private MongoTemplate mongoTemplate;
    public List<DoctorResponseDTO>doctorSearch(DoctorRequestDTO requestDTO){
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        //Search by doctor name
        if(requestDTO.getName()!=null &&  !requestDTO.getName().equals("")){
            criteriaList.add(Criteria.where("name").regex(Pattern.quote(requestDTO.getName().trim()),"i"));
        }
        // Search by specialisation
        if (requestDTO.getSpecialization() != null &&
                !requestDTO.getSpecialization().trim().isEmpty()) {

            criteriaList.add(
                    Criteria.where("specialisation")
                            .is(requestDTO.getSpecialization())
            );
        }

        // Search by state
        if (requestDTO.getState() != null &&
                !requestDTO.getState().trim().isEmpty()) {

            criteriaList.add(
                    Criteria.where("state")
                            .is(requestDTO.getState())
            );
        }

        // Search by city
        if (requestDTO.getCity() != null &&
                !requestDTO.getCity().trim().isEmpty()) {

            criteriaList.add(
                    Criteria.where("city")
                            .is(requestDTO.getCity())
            );
        }
        // Add all filters using AND
        if (!criteriaList.isEmpty()) {

            query.addCriteria(
                    new Criteria().andOperator(
                            criteriaList.toArray(new Criteria[0])
                    )
            );
        }
        // Get doctors from MongoDB
        List<DoctorDetails> doctors =
                mongoTemplate.find(query, DoctorDetails.class);
        // Convert Doctor -> DoctorResponseDTO
        return doctors.stream()
                .map(doctor -> new DoctorResponseDTO(
                        doctor.getId().toHexString(),
                        doctor.getName(),
                        doctor.getSpecialization(),
                        doctor.getState(),
                        doctor.getCity(),
                        doctor.getQualification(),
                        doctor.getPhone(),
                        doctor.getGender()
                ))
                .toList();
    }
}

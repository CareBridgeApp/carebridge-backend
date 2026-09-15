package com.carebridge.carebridge.Controller;

import com.carebridge.carebridge.Repository.DoctorRepo;
import com.carebridge.carebridge.Repository.PatientRepo;
import com.carebridge.carebridge.Repository.UserRepo;
import com.carebridge.carebridge.Utils.JWTutil;
import com.carebridge.carebridge.Dto.LoginEntry;
import com.carebridge.carebridge.entity.DoctorDetails;
import com.carebridge.carebridge.entity.PatientDetails;
import com.carebridge.carebridge.entity.UserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@RequestMapping("/api/auth")
@RestController
public class AuthController {
    @Autowired
    private JWTutil jwtUtil;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private DoctorRepo doctorRepo;
    @Autowired
    private PatientRepo patientRepo;
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null ||
                !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token expired or invalid");
        }
        String userId =
                jwtUtil.extractUserId(refreshToken);
        String newAccessToken =
                jwtUtil.generateToken(userId);
        ResponseCookie accessCookie =
                ResponseCookie.from(
                                "accessToken",
                                newAccessToken
                        )
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Lax")
                        .path("/")
                        .maxAge(Duration.ofMinutes(15))
                        .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                accessCookie.toString()
        );
        return ResponseEntity.ok("Access token refreshed");
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletResponse response) {

        ResponseCookie accessCookie =
                ResponseCookie.from("accessToken", "")
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Lax")
                        .path("/")
                        .maxAge(0)
                        .build();

        ResponseCookie refreshCookie =
                ResponseCookie.from("refreshToken", "")
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Lax")
                        .path("/api/auth/refresh")
                        .maxAge(0)
                        .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                accessCookie.toString()
        );

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                refreshCookie.toString()
        );

        return ResponseEntity.ok("Logged out");
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginEntry loginEntry) {
        try{
            UserDetails user=userRepo.findByEmail(loginEntry.getEmail());
            if (user==null){
                return new ResponseEntity<>(Map.of("message", "The User does not exists!"), HttpStatus.BAD_REQUEST);
            }
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginEntry.getEmail(), loginEntry.getPassword()));
            String accessToken = jwtUtil.generateToken(user.getId().toHexString());
            String refreshToken = jwtUtil.generateToken(user.getId().toHexString());
            ResponseCookie refreshCookie =
                    ResponseCookie.from("refreshToken", refreshToken)
                            .httpOnly(true)
                            .secure(false)
                            .sameSite("Lax")
                            .path("/api/auth")
                            .maxAge(Duration.ofDays(7))
                            .build();
            ResponseCookie accessCookie=ResponseCookie.from("jwt",accessToken)
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(Duration.ofHours(4))
                    .build();
            String role=user.getRole();
            String name=null;
            if(role=="doctor"){
                DoctorDetails doctor = doctorRepo.findByUserid(user.getId());
                name=doctor.getName();
            }else{
                PatientDetails patient = patientRepo.findByUserid(user.getId());
                name=patient.getName();
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(Map.of("role", role,"name",name));
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("message","Email or password is incorrect!"), HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        ObjectId id = userDetails.getId();
        UserDetails user=userRepo.findById(id).get();
        String role=user.getRole();
        if(Objects.equals(role, "patient")){
            PatientDetails patient=patientRepo.findByUserid(id);
            String username=patient.getName();
            return new  ResponseEntity<>(Map.of("name",username,"role",role), HttpStatus.OK);
        }
        if(Objects.equals(role, "doctor")){
            DoctorDetails doctor=doctorRepo.findByUserid(id);
            String username=doctor.getName();
            return new  ResponseEntity<>(Map.of("name",username,"role",role), HttpStatus.OK);
        }
        return new ResponseEntity<>(Map.of("message","The user or role is not found"), HttpStatus.OK);
    }
}

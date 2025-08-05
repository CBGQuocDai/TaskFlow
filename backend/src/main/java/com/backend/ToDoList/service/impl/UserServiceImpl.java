package com.backend.ToDoList.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.backend.ToDoList.dto.request.ChangeInfoRequest;
import com.backend.ToDoList.dto.request.ChangePasswordRequest;
import com.backend.ToDoList.dto.response.UserResponse;
import com.backend.ToDoList.entity.User;
import com.backend.ToDoList.errors.AppException;
import com.backend.ToDoList.enums.ErrorCode;
import com.backend.ToDoList.mapper.UserMapper;
import com.backend.ToDoList.repository.UserRepository;
import com.backend.ToDoList.service.UserService;
import com.backend.ToDoList.utils.EmailUtils;
import com.backend.ToDoList.utils.OtpUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RedisTemplate<String,Object> redisTemplate;
    private final OtpUtils otpUtils;
    private final EmailUtils emailUtils;
    private final AmazonS3 amazonS3;

    @Value("${aws.bucket-name}")
    private String BUCKET_NAME;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        log.info("loadUserByUsername");
        try {
            return userRepository.getByEmail(email);

        }catch (Exception e) {
            throw new UsernameNotFoundException("User not found");
        }
    }
    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        User u = userRepository.getByEmail(user.getEmail());
        if(passwordEncoder.matches(req.getOldPassword(), u.getPassword())) {
            u.setPassword(passwordEncoder.encode(req.getNewPassword()));
            userRepository.save(u);
        }
        else {
            throw new AppException(ErrorCode.PASSWORD_WRONG);
        }
    }
    @Override
    @Transactional
    public UserResponse getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return userMapper.userToUserResponse(user);
    }
    @Override
    @Transactional
    public UserResponse changeName(ChangeInfoRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        User u = userRepository.getByEmail(user.getEmail());
        u.setName(req.getName());
        return userMapper.userToUserResponse(userRepository.save(u));
    }
    @Override
    @Transactional
    public void changeEmail(String newEmail) {
        if(userRepository.existsByEmailAndActive(newEmail,true)) {
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }
        String otp = otpUtils.generateOtpCode();
        log.info(newEmail);
        emailUtils.sendEmail(newEmail,"verify new email",otp);
        redisTemplate.opsForValue().set(newEmail,otp,2, TimeUnit.MINUTES);
    }
    @Override
    @Transactional
    public UserResponse changeAvatar(MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.getByEmail(auth.getName());
        if(!Objects.equals(file.getContentType(), "image/jpeg")) {
            throw new AppException(ErrorCode.FILE_FAILED);
        }
        String originName = file.getOriginalFilename();

        if(originName==null) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        String[] temp = originName.split("//.");
        String fileName = user.getId()+"_"+new Date().getTime()+"."+temp[temp.length-1];

        log.info(fileName);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(BUCKET_NAME,fileName,file.getInputStream(), metadata);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.SERVER_ERROR);
        }
        user.setAvatarUrl(fileName);
        return userMapper.userToUserResponse(userRepository.save(user));
    }
}

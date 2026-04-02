package com.skillsBridge.skillBridge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;

import java.time.LocalDateTime;


@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id ;


    // used for the foreign  key
    @ManyToOne
    @JoinColumn(name ="user_id",nullable = false)
    private Users user ;

    @Column(nullable = false,unique = true)
    private  String token ;

    @Column(name ="expiry_date",nullable = false  )
    private LocalDateTime expiryDate ;

    public  boolean isExpired(){
        return LocalDateTime.now().isAfter(expiryDate);
    }
}

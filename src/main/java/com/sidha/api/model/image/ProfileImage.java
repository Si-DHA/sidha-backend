package com.sidha.api.model.image;


import com.sidha.api.model.UserModel;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue(value = "profile")
public class ProfileImage extends ImageData {
    @OneToOne
    private UserModel user;
}

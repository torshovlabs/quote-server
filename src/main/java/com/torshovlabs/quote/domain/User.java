package com.torshovlabs.quote.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Table(name = "app_user", indexes = {
//        @Index(name = "mpay_user_phone_number_idx", columnList = "phone_number")
})
public class User {

    @jakarta.persistence.Id
    @Column(length = 15)
    private String id;  //name as id here

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
}

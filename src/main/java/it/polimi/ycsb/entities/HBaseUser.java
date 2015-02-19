package it.polimi.ycsb.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Fabio Arcidiacono.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usertable", schema = "kundera@kundera_hbase_pu")
public class HBaseUser {

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SURNAME")
    private String surname;

    @Column(name = "AGE")
    private String age;

    @Column(name = "ADDRESS")
    private String address;
}

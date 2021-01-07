package com.aixoft.essample.aggregate;

import com.aixoft.escassandra.annotation.AggregateData;
import lombok.Value;
import lombok.With;

@Value
@AggregateData(tableName = "account_information")
public class AccountInformation {

    @With
    String userName;

    @With
    String email;

    @With
    int loyalPoints;

}
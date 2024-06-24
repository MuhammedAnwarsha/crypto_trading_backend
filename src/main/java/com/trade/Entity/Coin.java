package com.trade.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Coin {

    @Id
    private String id;

    private String symbol;

    private String name;

    private String image;

    private double currentPrice;

    private long marketCap;

    private int marketCapRank;

    private long fullyDilutedValuation;

    private long totalVolume;

    private double high24h;

    private double low24h;

    private double priceChange24h;

    private double priceChangePercentage24h;

    private long marketCapChange24h;

    private double marketCapChangePercentage24h;

    private long circulatingSupply;

    private long totalSupply;

    private long maxSupply;

    private double ath;

    private double athChangePercentage;

    private Date athDate;

    private double atl;

    private double atlChangePercentage;

    private Date atlDate;

    @JsonIgnore
    private String roi;

    private Date lastUpdated;
}

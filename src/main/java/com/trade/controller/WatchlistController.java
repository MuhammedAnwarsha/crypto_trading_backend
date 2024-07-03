package com.trade.controller;

import com.trade.Entity.Coin;
import com.trade.Entity.User;
import com.trade.Entity.Watchlist;
import com.trade.service.CoinService;
import com.trade.service.UserService;
import com.trade.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    @Autowired
    private UserService userService;

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private CoinService coinService;

    @GetMapping("/user")
    public ResponseEntity<Watchlist> getUserWatchlist(@RequestHeader("Authorization")String jwt)throws Exception{

        User user = userService.findUserProfileByJwt(jwt);
        Watchlist watchlist = watchlistService.findUserWatchlist(user.getId());

        return ResponseEntity.ok(watchlist);
    }

    @GetMapping("/{watchlistId}")
    public ResponseEntity<Watchlist> getWatchlistById(@RequestHeader("Authorization")String jwt,
                                                      @PathVariable Long watchlistId)throws Exception{

        User user = userService.findUserProfileByJwt(jwt);
        Watchlist watchlist = watchlistService.findById(watchlistId);

        return ResponseEntity.ok(watchlist);
    }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<Coin> addItemToWatchlist(@RequestHeader("Authorization")String jwt,
                                                   @PathVariable String coinId)throws Exception{

        User user = userService.findUserProfileByJwt(jwt);
        Coin coin = coinService.findById(coinId);
        Coin addedCoin = watchlistService.addItemToWatchlist(coin,user);

        return ResponseEntity.ok(addedCoin);
    }
}

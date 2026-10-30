package com.auction.common.patterns;

import com.auction.common.model.*;
import java.time.LocalDateTime;

public class ItemFactory {
    public static Item createItem(ItemCategory category, String id, LocalDateTime createdAt,
                                  String name, String description, double startingPrice, double currentPrice,
                                  String sellerId , Object... extraArgs) {
        return switch (category) {
            case ELECTRONICS ->{
                String brand = (extraArgs.length > 0 && extraArgs[0] instanceof String b)? b:"Chưa xác định";
                int warranty = (extraArgs.length > 1 && extraArgs[1] instanceof Integer w)? w : 12;
                yield new Electronics(id,createdAt,name,description,startingPrice,currentPrice,sellerId,brand,warranty);
            }
            case ART         -> {
                String artist = (extraArgs.length > 0 && extraArgs[0] instanceof String a)?a:"Chưa xác định";
                int year = (extraArgs.length > 1 && extraArgs[1] instanceof Integer y)?y:2000;
                yield new Art(id,createdAt,name,description,startingPrice,currentPrice,sellerId,artist,year);
            }
            case VEHICLE     -> {
                int modelYear = (extraArgs.length > 0 && extraArgs[0] instanceof Integer my)? my:2020;
                double mileage = (extraArgs.length > 1 && extraArgs[1] instanceof Double m)?m:0.0;
                yield new Vehicle(id,createdAt,name,description,startingPrice,currentPrice,sellerId,modelYear,mileage);
            }
            default          ->
                    throw new IllegalArgumentException("Loại sản phẩm không hợp lệ: " + category);
        };
    }
}
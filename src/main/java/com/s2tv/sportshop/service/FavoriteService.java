package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.response.FavoriteUpdateResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.model.Favorite;
import com.s2tv.sportshop.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;

    public FavoriteUpdateResponse updateFavourite(String userId, String productId) {
        Favorite favorite = favoriteRepository.findByUserId(userId)
                .orElse(
                        Favorite.builder()
                                .userId(userId)
                                .products(new ArrayList<>())
                                .build()
                );

        List<String> products = favorite.getProducts();

        if(products.contains(productId)) {
            products.remove(productId);
        }
        else {
            products.add(productId);
        }
        favorite.setProducts(products);
        favoriteRepository.save(favorite);

        return FavoriteUpdateResponse.builder()
                .userId(favorite.getUserId())
                .products(favorite.getProducts())
                .build();
    }

    public List<String> getFavourite(String userId) {
        Optional<Favorite> optional = favoriteRepository.findByUserId(userId);

        if (optional.isEmpty()) {
            throw new AppException(ErrorCode.FAVORITE_NOT_FOUND);
        }

        List<String> products = optional.get().getProducts();
        if (products == null || products.isEmpty()) {
            throw new AppException(ErrorCode.FAVORITE_EMPTY);
        }

        return products;
    }

    public void clearFavourites(String userId) {
        Favorite favorite = favoriteRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.FAVORITE_NOT_FOUND));

        favorite.setProducts(new ArrayList<>());
        favoriteRepository.save(favorite);
    }
}

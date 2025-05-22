package com.s2tv.sportshop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s2tv.sportshop.dto.request.StoreCreateRequest;
import com.s2tv.sportshop.dto.request.StoreUpdateRequest;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.model.Store;
import com.s2tv.sportshop.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StoreService {
    StoreRepository storeRepository;
    CloudinaryService cloudinaryService;

    public Store createStore(StoreCreateRequest request, MultipartHttpServletRequest multipartRequest) throws IOException {
        if (request.getStoreAddress() == null || request.getStorePhone() == null || request.getStoreEmail() == null) {
            throw new AppException(ErrorCode.STORE_INFO_REQUIRED);
        }

        List<MultipartFile> files = multipartRequest.getFiles("files");

        List<String> bannerUrls = new ArrayList<>();
        for (MultipartFile file : files) {
                if (file.getContentType() != null && file.getContentType().startsWith("image/")) {
                    String url = cloudinaryService.uploadFile(file, "store", "image");
                    bannerUrls.add(url);
                }
        }

        Store store = Store.builder()
                .storeBanner(bannerUrls)
                .storeAddress(request.getStoreAddress())
                .storePhone(request.getStorePhone())
                .storeEmail(request.getStoreEmail())
                .build();

        return storeRepository.save(store);
    }

    public Store updateStore(String storeId, StoreUpdateRequest request, MultipartHttpServletRequest multipartRequest) throws IOException {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));

        List<String> bannersToKeep;
        if (request.getExistingBanners() != null && !request.getExistingBanners().isBlank()) {
            ObjectMapper mapper = new ObjectMapper();
            bannersToKeep = mapper.readValue(request.getExistingBanners(), new TypeReference<>() {});
        } else {
            bannersToKeep = new ArrayList<>(store.getStoreBanner());
        }


        List<MultipartFile> files = multipartRequest.getFiles("files");
        List<String> newBanners = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.getContentType() != null && file.getContentType().startsWith("image/")) {
                String url = cloudinaryService.uploadFile(file, "store", "image");
                newBanners.add(url);
            }
        }

        if (request.getStoreAddress() != null && !request.getStoreAddress().isBlank()) {
            store.setStoreAddress(request.getStoreAddress());
        }

        if (request.getStorePhone() != null && !request.getStorePhone().isBlank()) {
            store.setStorePhone(request.getStorePhone());
        }

        if (request.getStoreEmail() != null && !request.getStoreEmail().isBlank()) {
            store.setStoreEmail(request.getStoreEmail());
        }

        store.setStoreBanner(Stream.concat(bannersToKeep.stream(), newBanners.stream()).collect(Collectors.toList()));

        return storeRepository.save(store);
    }

    public Store getDetailStore(String storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));

        return store;
    }
}

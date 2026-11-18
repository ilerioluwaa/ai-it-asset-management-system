package com.cmsc495.assetmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmsc495.assetmanagement.model.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long> {

}

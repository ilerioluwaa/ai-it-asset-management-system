
package com.cmsc495.assetmanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cmsc495.assetmanagement.model.Asset;
import com.cmsc495.assetmanagement.repository.AssetRepository;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetRepository assetRepository;

    public AssetController(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @GetMapping
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }
    @GetMapping("/{id}")
    public Asset getAssetById(@PathVariable Long id) {
        return assetRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Asset not found"
            ));
    }
    @PostMapping
    public Asset createAsset(@RequestBody Asset asset) {
        return assetRepository.save(asset);
    }

    @PutMapping("/{id}")
    public Asset updateAsset(@PathVariable Long id,
                            @RequestBody Asset updatedAsset) {

    Asset existingAsset = assetRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Asset not found"
        ));

    existingAsset.setName(updatedAsset.getName());
    existingAsset.setCategory(updatedAsset.getCategory());
    existingAsset.setSerialNumber(updatedAsset.getSerialNumber());
    existingAsset.setStatus(updatedAsset.getStatus());
    existingAsset.setAssignedTo(updatedAsset.getAssignedTo());

    return assetRepository.save(existingAsset);
    }
    @DeleteMapping("/{id}")
    public void deleteAsset(@PathVariable Long id) {

        if (!assetRepository.existsById(id)) {
            throw new RuntimeException("Asset not found");
        }

        assetRepository.deleteById(id);
    }
}

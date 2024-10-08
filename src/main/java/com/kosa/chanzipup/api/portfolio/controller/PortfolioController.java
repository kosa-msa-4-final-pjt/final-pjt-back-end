package com.kosa.chanzipup.api.portfolio.controller;

import com.kosa.chanzipup.api.portfolio.controller.request.PortfolioRegisterRequest;
import com.kosa.chanzipup.api.portfolio.controller.request.PortfolioUpdateRequest;
import com.kosa.chanzipup.api.portfolio.controller.response.PortfolioDetailResponse;
import com.kosa.chanzipup.api.portfolio.controller.response.PortfolioEditResponse;
import com.kosa.chanzipup.api.portfolio.controller.response.PortfolioListResponse;
import com.kosa.chanzipup.api.portfolio.controller.response.PortfolioRegisterResponse;
import com.kosa.chanzipup.api.portfolio.service.PortfolioImageService;
import com.kosa.chanzipup.api.portfolio.service.PortfolioService;
import com.kosa.chanzipup.application.Page;
import com.kosa.chanzipup.application.images.ImageService;
import com.kosa.chanzipup.config.security.userdetail.UnifiedUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
@Slf4j
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final ImageService imageService;
    private final PortfolioImageService portfolioImageService;

    @PostMapping("/create")
    public ResponseEntity<?> addPortfolio(
            @Valid @RequestBody PortfolioRegisterRequest portfolioRequest,
            @AuthenticationPrincipal UnifiedUserDetails userDetails
    ) {
        String email = userDetails.getUsername();


        try {
            // 포트폴리오 저장 서비스 호출
            PortfolioRegisterResponse savedPortfolioResponse = portfolioService.registerPortfolio(portfolioRequest, email);
            return ResponseEntity.ok(savedPortfolioResponse);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to save portfolio");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 시공사례 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<List<PortfolioListResponse>> listPortfolios() {
        List<PortfolioListResponse> portfolios = portfolioService.getAllPortfolios();
        return ResponseEntity.ok(portfolios);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<List<PortfolioListResponse>>> listPortfoliosWithPage(Pageable pageable) {
        return ResponseEntity.ok(portfolioService.getAllPortfoliosWithPage(pageable.getPageNumber(), pageable.getPageSize()));
    }

    @GetMapping("/mypage")
    public ResponseEntity<Page<List<PortfolioListResponse>>> listPortfoliosWithPage(Pageable pageable,
                                                                                    @AuthenticationPrincipal UnifiedUserDetails userDetails) {
        return ResponseEntity.ok(portfolioService.getMyPagePortfoliosWithPage(pageable.getPageNumber(),
                pageable.getPageSize(), userDetails.getUsername()));
    }


    // 시공사례 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDetailResponse> getPortfolioById(@PathVariable long id,
                                                                    @AuthenticationPrincipal UnifiedUserDetails userDetails) {

        PortfolioDetailResponse portfolio = portfolioService.getPortfolioById(id, userDetails);
        return ResponseEntity.ok(portfolio);
    }

    @PostMapping("/{portfolioId}/images")
    public ResponseEntity<String> uploadReviewImages(@PathVariable("portfolioId") Long portfolioId,
                                                     MultipartFile file) {
        String name = file.getName();
        String uploadEndPoint = imageService.store("portfolio", file);
        String uploadFullPath = portfolioImageService.addPortfolioImage(portfolioId, uploadEndPoint);
        log.info("name = {}, uploadFullPath = {}", name, uploadFullPath);
        return ResponseEntity.ok(uploadFullPath);
    }

    @PatchMapping("/{portfolioId}")
    public ResponseEntity<String> updatePortfolio(@PathVariable("portfolioId") Long portfolioId,
                                                  @Valid @RequestBody PortfolioUpdateRequest updateRequest,
                                                  @AuthenticationPrincipal UnifiedUserDetails userDetails) {
        String updateContent = portfolioService.updatePortfolio(portfolioId, updateRequest);
        return ResponseEntity.ok(updateContent);
    }

    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Boolean> deletePortfolio(@PathVariable("portfolioId") Long portfolioId,
                                                        @AuthenticationPrincipal UnifiedUserDetails userDetails) {
        List<String> deleteUploadImages = portfolioService.deletePortfolio(portfolioId, userDetails.getUsername());
        imageService.deleteAllImages(deleteUploadImages);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/{portfolioId}/edit")
    public ResponseEntity<PortfolioEditResponse> getPortfolioForUpdate(@AuthenticationPrincipal UnifiedUserDetails userDetails,
                                                                       @PathVariable Long portfolioId) {
        return ResponseEntity.ok(portfolioService.getPortfolioDetailForUpdate(userDetails.getUsername(), portfolioId));
    }
}

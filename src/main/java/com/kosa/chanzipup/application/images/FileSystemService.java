package com.kosa.chanzipup.application.images;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class FileSystemService implements ImageService {
    private final Path rootLocation;
    private final String location;
    private final String domainAddress;

    public FileSystemService(@Value("${file.location}") String location,
                             @Value("${domain.address}") String domainAddress) {
        this.rootLocation = Paths.get(location);
        this.location = location;
        this.domainAddress = domainAddress;
        init(rootLocation);
    }

    @Override
    public String store(String detailPathLocation, MultipartFile file) {
        Path detailPath = rootLocation.resolve(detailPathLocation);
        init(detailPath);
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            Path destinationFile = detailPath.resolve(Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(detailPath.toAbsolutePath())) {
                // This is a security check
                throw new RuntimeException(
                        "Cannot store file outside current directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }

            String savedFilePath = destinationFile.toString()
                    .replace(location, "")
                    .replace("\\", "/");

            log.info("savedFilePath = {}", savedFilePath);
            return String.format("/images%s", savedFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    @Override
    public Resource loadAsResource(String subPath, String fileName) {
        try {
            Path file = rootLocation.resolve(subPath).resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException(
                    "Could not read file: " + fileName);

        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + fileName, e);
        }
    }

    @Override
    public void deleteAllImages(List<String> deleteImageUrls) {
        List<String> imageSaveUrls = deleteImageUrls.stream()
                .map(this::doResourceMatching)
                .toList();

        for (String imageSaveUrl : imageSaveUrls) {
            log.info("{} ", imageSaveUrl);
//            deleteImage(imageSaveUrl);
        }
    }

    public String doResourceMatching(String url) {
        return url.replace(domainAddress, location);
    }

//    private void deleteImage(String imageSaveUrl) {
//        Path detailPath = rootLocation.resolve(detailPathLocation);
//        init(detailPath); // 경로 초기화, 필요할 경우
//        try {
//            // 삭제할 파일 경로 생성
//            Path fileToDelete = detailPath.resolve(Paths.get(fileName)).normalize().toAbsolutePath();
//
//            // 보안 검사: 파일이 디렉토리 외부에 있는지 확인
//            if (!fileToDelete.getParent().equals(detailPath.toAbsolutePath())) {
//                throw new RuntimeException("Cannot delete file outside current directory.");
//            }
//
//            // 파일 존재 여부 확인
//            if (!Files.exists(fileToDelete)) {
//                throw new RuntimeException("File not found: " + fileName);
//            }
//
//            // 파일 삭제 시도
//            Files.delete(fileToDelete);
//
//            // 로그에 삭제 성공 메시지 기록
//            log.info("Deleted file: {}", fileToDelete.toString());
//
//            // 삭제된 파일 경로 반환 (필요 시)
//            String deletedFilePath = fileToDelete.toString()
//                    .replace(location, "")
//                    .replace("\\", "/");
//            return String.format("File successfully deleted: /images%s", deletedFilePath);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to delete file: " + fileName, e);
//        }
//    }

    private void init(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

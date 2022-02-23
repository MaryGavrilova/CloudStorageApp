package ru.netology.cloudstorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.CloudFileInfo;
import ru.netology.cloudstorage.dto.CloudFileName;
import ru.netology.cloudstorage.service.FilesStorageService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
//@RequestMapping("/cloud")
public class FilesStorageController {
    private final FilesStorageService filesStorageService;

    // upload file to cloud storage
    @PostMapping(path = "/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadFile(@RequestHeader("auth-token") String authToken,
                                             @RequestParam("filename") @NotBlank String fileName,
                                             @RequestParam("file") @NotNull MultipartFile file) throws IOException {

        filesStorageService.storeFile(fileName, file);
       return new ResponseEntity<>("File is uploaded successfully",
                HttpStatus.OK);
    }

    // delete file in cloud storage
    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestHeader("auth-token") String authToken,
                           @RequestParam("filename") @NotBlank String fileName) {
        filesStorageService.deleteFile(fileName);
        return new ResponseEntity<>("File is deleted successfully",
                HttpStatus.OK);
    }

    // download file from cloud storage
    @GetMapping(path = "/file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public @ResponseBody
    byte[] downloadFile(@RequestHeader("auth-token") String authToken,
                        @RequestParam("filename") @NotBlank String fileName) {
        return filesStorageService.getFile(fileName);
    }

    // edit file name in cloud storage
    @PutMapping("/file")
    public ResponseEntity<String> editFileName(@RequestHeader("auth-token") String authToken,
                             @RequestParam("filename") @NotBlank String currentFileName,
                             @Valid @RequestBody CloudFileName newCloudFileName) {
        filesStorageService.editFileName(currentFileName, newCloudFileName);
        return new ResponseEntity<>("File name  is edited successfully",
                HttpStatus.OK);
    }

    // get limited file list
    @GetMapping("/list")
    public List<CloudFileInfo> getInfoAboutAllFiles(@RequestHeader("auth-token") String authToken,
                                                    @RequestParam("limit") @Min(1) Integer limit) {
        return filesStorageService.getInfoAboutAllFiles(limit);
    }
}

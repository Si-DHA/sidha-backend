package com.sidha.api.controller;

import com.sidha.api.DTO.request.InsidenDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.model.Insiden;
import com.sidha.api.model.Insiden.InsidenStatus;
import com.sidha.api.model.image.ImageData;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.repository.OrderItemDb;
import com.sidha.api.service.InsidenService;
import com.sidha.api.service.StorageService;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/insiden")
public class InsidenController {

    @Autowired
    private InsidenService insidenService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private OrderItemDb orderItemDb;

    @PostMapping("/create")
    public ResponseEntity<?> createInsiden(
            @RequestParam("sopirId") UUID sopirId,
            @RequestParam("kategori") String kategori,
            @RequestParam("lokasi") String lokasi,
            @RequestParam("keterangan") String keterangan, @RequestParam("orderItemId") String orderItemId,
            @RequestPart(value = "buktiFoto", required = false) MultipartFile buktiFoto) {
        try {

            OrderItem orderItem = orderItemDb.findById(orderItemId)
                    .orElseThrow(() -> new RuntimeException("Order Item not found"));

            Insiden insiden = new Insiden();
            insiden.setKategori(kategori);
            insiden.setLokasi(lokasi);
            insiden.setKeterangan(keterangan);
            insiden.setOrderItem(orderItem);
            Insiden createdInsiden = insidenService.createInsiden(insiden, sopirId, orderItemId, buktiFoto);
            return new ResponseEntity<>(createdInsiden, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateInsiden(
            @PathVariable UUID id,
            @RequestParam("kategori") String kategori,
            @RequestParam("lokasi") String lokasi,
            @RequestParam("keterangan") String keterangan, @RequestParam("orderItemId") String orderItemId,
            @RequestPart(value = "buktiFoto", required = false) MultipartFile buktiFoto) {
        try {
            Insiden insidenDetails = new Insiden();
            insidenDetails.setKategori(kategori);
            insidenDetails.setLokasi(lokasi);
            insidenDetails.setKeterangan(keterangan);
            Insiden updatedInsiden = insidenService.updateInsiden(id, insidenDetails, orderItemId, buktiFoto);
            return ResponseEntity.ok(updatedInsiden);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to update image: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInsiden(@PathVariable UUID id) {
        try {
            insidenService.deleteInsiden(id);
            return ResponseEntity
                    .ok("Insiden is deleted!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateInsidenStatus(
            @PathVariable UUID id,
            @RequestParam("status") InsidenStatus status) {
        try {
            Insiden updatedInsiden = insidenService.updateInsidenStatus(id, status);
            return ResponseEntity.ok(updatedInsiden);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<InsidenDTO>> getAllInsidens() {
        List<InsidenDTO> insidens = insidenService.getAllInsidensWithSopirInfo();
        return ResponseEntity.ok(insidens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInsidenById(@PathVariable UUID id) {
        try {
            Insiden insiden = insidenService.getInsidenById(id);
            return ResponseEntity.ok(insiden);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, 404, e.getMessage(), null));
        }
    }

    @GetMapping("/sopir/{sopirId}")
    public ResponseEntity<?> getInsidensBySopirId(@PathVariable UUID sopirId) {
        try {
            List<Insiden> insidens = insidenService.getInsidensBySopirId(sopirId);
            return ResponseEntity.ok(insidens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, 404, e.getMessage(), null));
        }
    }

    @GetMapping("/bukti/{id}")
    public ResponseEntity<?> getBuktiFoto(@PathVariable UUID id) {
        try {
            ImageData imageData = insidenService.getBuktiFotoById(id);
            if (imageData != null) {
                byte[] image = storageService.getImageFromFileSystem(imageData.getName());
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.valueOf(imageData.getType()))
                        .body(image);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to fetch image");
        }
    }

    @GetMapping("/total/today")
    public ResponseEntity<?> getTotalInsidenForToday() {
        return ResponseEntity
                .ok(new BaseResponse<>(true, 200, "Total insiden for today", insidenService.getTotalInsidenForToday()));
    }

    @GetMapping("/total/week")
    public ResponseEntity<?> getTotalInsidenForThisWeek() {
        return ResponseEntity.ok(new BaseResponse<>(true, 200, "Total insiden for this week",
                insidenService.getTotalInsidenForThisWeek()));
    }

    @GetMapping("/total/month")
    public ResponseEntity<?> getTotalInsidenForThisMonth() {
        return ResponseEntity.ok(new BaseResponse<>(true, 200, "Total insiden for this month",
                insidenService.getTotalInsidenForThisMonth()));
    }

    @GetMapping("/total/year")
    public ResponseEntity<?> getTotalInsidenForThisYear() {
        return ResponseEntity.ok(new BaseResponse<>(true, 200, "Total insiden for this year",
                insidenService.getTotalInsidenForThisYear()));
    }

    @GetMapping("/total/all")
    public ResponseEntity<?> getTotalInsiden() {
        return ResponseEntity.ok(new BaseResponse<>(true, 200, "Total insiden", insidenService.getTotalInsiden()));
    }

    @GetMapping("/total/weekly")
    public ResponseEntity<?> getWeeklyTotalInsidenInMonth(@RequestParam int year, @RequestParam int month) {
        return ResponseEntity.ok(new BaseResponse<>(true, 200, "Weekly total insiden in month",
                insidenService.getWeeklyTotalInsidenInMonth(year, month)));
    }

    @GetMapping("/total/monthly")
    public ResponseEntity<?> getMonthlyTotalInsidenInYear(@RequestParam int year) {
        return ResponseEntity.ok(new BaseResponse<>(true, 200, "Monthly total insiden in year",
                insidenService.getMonthlyTotalInsidenInYear(year)));
    }

    @GetMapping("/total/yearly")
    public ResponseEntity<?> getYearlyTotalInsidenInRange(@RequestParam int startYear, @RequestParam int endYear) {
        return ResponseEntity.ok(new BaseResponse<>(true, 200, "Yearly total insiden in range",
                insidenService.getYearlyTotalInsidenInRange(startYear, endYear)));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getListInsiden(@RequestParam String timeRange) {
        try {
            List<Insiden> listInsiden = null;
            if (timeRange.equals("today"))
                listInsiden = insidenService.getListInsidenForToday();
            else if (timeRange.equals("week"))
                listInsiden = insidenService.getListInsidenForThisWeek();
            else if (timeRange.equals("month"))
                listInsiden     = insidenService.getListInsidenForThisMonth();
            else if (timeRange.equals("year"))
                listInsiden = insidenService.getListInsidenForThisYear();
            return ResponseEntity.ok(new BaseResponse<>(true, 200, "List of insiden", listInsiden));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse<>(true, 404, "Failed", e.getMessage()));
        }
    }

}

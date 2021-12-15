package com.webclient.externalService;

import com.webclient.dto.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("externalService")
@Slf4j
public class ExternalServiceController {

    private static final List<Data> dataList;

    static {
        dataList = new ArrayList<>();
        dataList.add(new Data(1, "Data - 1"));
        dataList.add(new Data(2, "Data - 2"));
        dataList.add(new Data(3, "Data - 3"));
    }

    @GetMapping
    public ResponseEntity<List<Data>> getDataList() {
        log.info("Getting data list");
        return new ResponseEntity<>(dataList, HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
    @PostMapping
    public ResponseEntity postData(@RequestBody Data data) {
        for (Data currentData : dataList) {
            if (currentData.getId() == data.getId()) {
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        }
        dataList.add(data);
        log.info("Posted new data - {}", data.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<List<Data>> updateData(@RequestBody Data data) {
        dataList
                .stream()
                .filter(currentData -> currentData.getId() == data.getId())
                .findFirst()
                .ifPresent(dataList::remove);
        dataList.add(data);
        log.info("Updated data - {}", data.getId());
        return new ResponseEntity<>(dataList, HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteData(@PathVariable int id) {
        dataList
                .stream()
                .filter(currentData -> currentData.getId() == id)
                .findFirst()
                .ifPresent(dataList::remove);

        log.info("Deleted data - {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

//package com.majesteye.skeye.skeyepredict.eedaserver.app.runner;
//
//import com.majesteye.skeye.skeyepredict.eedaserver.util.Indexer;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Component;
//
///**
// * @author Rabie Saidi
// */
//@Component
//@ConditionalOnProperty(name = "task", havingValue = "index")
//@AllArgsConstructor
//@Slf4j
//public class RunIndexer implements ApplicationRunner {
//    @Autowired
//    private final Indexer indexer;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        indexer.index();
//    }
//}

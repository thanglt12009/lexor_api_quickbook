package com.lexor.qbsa.service.queue;

import com.lexor.qbsa.controller.WebhooksController;
import com.lexor.qbsa.domain.PayloadQueue;
import com.lexor.qbsa.repository.PayloadQueueRepository;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.jvnet.hk2.annotations.Service;

/**
 * Manages a queue and executes a single async thread to process the queue
 * whenever an item is added to the queue
 *
 * @author dderose
 *
 */
@Service
public class QueueService {

    private static final java.util.logging.Logger LOG = Logger.getLogger(WebhooksController.class.getName());

    @Inject
    private PayloadQueueRepository payloadQueueRepository;

    @Inject
    private QueueProcessor queueProcessor;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        // intitialize a single thread executor, this will ensure only one thread processes the queue
        executorService = Executors.newSingleThreadExecutor();
    }

    public void add(String source, String payload) {

        // add payload to database
        PayloadQueue p = new PayloadQueue(source, payload, 0);
        try {
            payloadQueueRepository.persist(p);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "{0}-{1}", new Object[]{source, payload});
            LOG.log(Level.SEVERE, "{0}", ex);
        }

        //Call executor service
        executorService.submit(queueProcessor);
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }

}

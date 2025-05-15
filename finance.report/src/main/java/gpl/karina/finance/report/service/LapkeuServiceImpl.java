package gpl.karina.finance.report.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import gpl.karina.finance.report.repository.LapkeuRepository;
import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
public class LapkeuServiceImpl implements LapkeuService {

    private final LapkeuRepository lapkeuRepository;
    private final HttpServletRequest request;

    public LapkeuServiceImpl(LapkeuRepository lapkeuRepository, HttpServletRequest request) {
        this.lapkeuRepository = lapkeuRepository;
        this.request = request;
    }

    public String getTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Implement the methods defined in the LapkeuService interface here
    
}

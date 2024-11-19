package net.purocodigo.encuestabackend.utils.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.purocodigo.encuestabackend.interfaces.PollResult;
import net.purocodigo.encuestabackend.models.responses.PollResultRest;
import net.purocodigo.encuestabackend.models.responses.ResultDetailRest;

public class PollResultTransformer implements Transformer<List<PollResult>, List<PollResultRest>> {

    @Override
    public List<PollResultRest> transformData(List<PollResult> data) {
        
        Map<String, PollResultRest> transformedData = new HashMap<String, PollResultRest>();

        for (PollResult result: data) {
            PollResultRest pollResult;

            String key = Long.toString(result.getQuestionId());

            if(!transformedData.containsKey(key)) {
                List<ResultDetailRest> details = new ArrayList<ResultDetailRest>();
                details.add(new ResultDetailRest(result.getAnswer(), result.getResult()));
                pollResult = new PollResultRest(result.getQuestion(), details);
                transformedData.put(key, pollResult);
            } else {
                pollResult = transformedData.get(key);
                pollResult.getDetails().add(new ResultDetailRest(result.getAnswer(), result.getResult()));
            }

        }

        List<PollResultRest> resultsList = new ArrayList<>(transformedData.values());

        return resultsList;
    }
    
}

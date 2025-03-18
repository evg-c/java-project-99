package hexlet.code.service;

import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    /**
     * Метод создания меток при начальной инициализации.
     * @param name - имя метки, передаваемый из DataInitializer.
     */
    public void createLabel(String name) {

        var taskStatus = new Label();
        taskStatus.setName(name);
        labelRepository.save(taskStatus);
    }
}

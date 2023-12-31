package pl.ms.projectoverview.web.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.ms.projectoverview.app.converters.ProjectPlanConverter;
import pl.ms.projectoverview.app.entitites.ProjectPlan;
import pl.ms.projectoverview.app.exceptions.NotCurrentUserProjectException;
import pl.ms.projectoverview.app.exceptions.NotCurrentUserProjectPlanException;
import pl.ms.projectoverview.app.exceptions.TitleNotFoundException;
import pl.ms.projectoverview.app.exceptions.UserNotFoundException;
import pl.ms.projectoverview.app.services.ProjectPlanService;
import pl.ms.projectoverview.web.models.ProjectModel;
import pl.ms.projectoverview.web.models.ProjectPlanModel;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static pl.ms.projectoverview.app.converters.ProjectPlanConverter.convertModelToApp;
import static pl.ms.projectoverview.app.converters.ProjectPlanConverter.convertToModel;
import static pl.ms.projectoverview.app.converters.ProjectConverter.convertToModel;

@RestController
@CrossOrigin
@Validated
public class ProjectPlanController {

    private final ProjectPlanService mProjectPlanService;

    public ProjectPlanController(ProjectPlanService projectPlanService) {
        mProjectPlanService = projectPlanService;
    }

    @PostMapping("/api/v1/project/plan/create")
    public ProjectPlanModel createPlan(@RequestBody @Valid ProjectPlanModel planModel) throws UserNotFoundException {
        return convertToModel(mProjectPlanService.createPlan(convertModelToApp(planModel)));
    }

    @PutMapping("/api/v1/project/plan/update")
    public ProjectPlanModel updatePlan(@RequestBody @Valid ProjectPlanModel planModel)
            throws UserNotFoundException, NotCurrentUserProjectPlanException {
        return convertToModel(mProjectPlanService.updatePlan(convertModelToApp(planModel)));
    }

    @DeleteMapping("/api/v1/project/plan/delete")
    public void deletePlan(@RequestParam("id") @Valid @Min(0) Integer projectPlanId) throws NotCurrentUserProjectPlanException {
        mProjectPlanService.deletePlan(projectPlanId);
    }

    @GetMapping("/api/v1/project/plan/filter")
    public List<ProjectPlanModel> filterQuery(
            @RequestParam(name = "language", required = false) @Valid @NotBlank String language
    ) {
        return convertToModel(mProjectPlanService.filterQuery(language));
    }

    @GetMapping("/api/v1/project/plan/get")
    public List<ProjectPlanModel> getUserProjectPlans() {
        return convertToModel(mProjectPlanService.getUserProjectPlans());
    }

    @GetMapping("/api/v1/project/plan/get/title")
    public ProjectPlanModel getByTitle(@RequestParam("title") @Valid @NotBlank String title)
            throws TitleNotFoundException, NotCurrentUserProjectException {
        return convertToModel(mProjectPlanService.getByTitle(title));
    }
}

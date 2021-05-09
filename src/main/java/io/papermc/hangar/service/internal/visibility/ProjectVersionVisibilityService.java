package io.papermc.hangar.service.internal.visibility;

import io.papermc.hangar.db.dao.HangarDao;
import io.papermc.hangar.db.dao.internal.table.VisibilityDAO;
import io.papermc.hangar.db.dao.internal.table.versions.ProjectVersionsDAO;
import io.papermc.hangar.model.common.projects.Visibility;
import io.papermc.hangar.model.db.versions.ProjectVersionTable;
import io.papermc.hangar.model.db.visibility.ProjectVersionVisibilityChangeTable;
import io.papermc.hangar.model.internal.job.UpdateDiscourseProjectTopicJob;
import io.papermc.hangar.model.internal.logs.LogAction;
import io.papermc.hangar.model.internal.logs.contexts.VersionContext;
import io.papermc.hangar.service.internal.JobService;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map.Entry;

@Service
public class ProjectVersionVisibilityService extends VisibilityService<ProjectVersionTable, ProjectVersionVisibilityChangeTable> {

    private final ProjectVersionsDAO projectVersionsDAO;
    private final VisibilityDAO visibilityDAO;
    private final JobService jobService;

    @Autowired
    public ProjectVersionVisibilityService(HangarDao<VisibilityDAO> visibilityDAO, HangarDao<ProjectVersionsDAO> projectVersionDAO, JobService jobService) {
        super(ProjectVersionVisibilityChangeTable::new);
        this.visibilityDAO = visibilityDAO.get();
        this.projectVersionsDAO = projectVersionDAO.get();
        this.jobService = jobService;
    }

    @Override
    void logVisibilityChange(ProjectVersionTable model, Visibility oldVisibility) {
        userActionLogService.version(LogAction.VERSION_VISIBILITY_CHANGED.create(VersionContext.of(model.getProjectId(), model.getId()), model.getVisibility().getName(), oldVisibility.getName()));
    }

    @Override
    void updateLastVisChange(long currentUserId, long modelId) {
        visibilityDAO.updateLatestVersionChange(currentUserId, modelId);
    }

    @Override
    ProjectVersionTable getModel(long id) {
        return null;
    }

    @Override
    ProjectVersionTable updateModel(ProjectVersionTable model) {
        return projectVersionsDAO.update(model);
    }

    @Override
    void insertNewVisibilityEntry(ProjectVersionVisibilityChangeTable visibilityTable) {
        visibilityDAO.insert(visibilityTable);
    }

    @Override
    protected void postUpdate(@Nullable ProjectVersionTable model) {
        if (model != null) {
            jobService.save(new UpdateDiscourseProjectTopicJob(model.getProjectId()));
        }
    }

    @Override
    public Entry<String, ProjectVersionVisibilityChangeTable> getLastVisibilityChange(long principalId) {
        return visibilityDAO.getLatestVersionVisibilityChange(principalId);
    }
}


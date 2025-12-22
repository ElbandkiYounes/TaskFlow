import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { projectsApi, tasksApi } from '@/services/api';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { Progress } from '@/components/ui/progress';
import { Plus, FolderKanban, Pencil, Trash2 } from 'lucide-react';
import type { Project, Task } from '@/types';
import CreateProjectDialog from '@/components/projects/CreateProjectDialog';
import { EditProjectDialog } from '@/components/projects/EditProjectDialog';
import { DeleteProjectDialog } from '@/components/projects/DeleteProjectDialog';

export default function Projects() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [projectTasks, setProjectTasks] = useState<Record<number, Task[]>>({});
  const [isLoading, setIsLoading] = useState(true);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingProject, setEditingProject] = useState<Project | null>(null);
  const [deletingProject, setDeletingProject] = useState<Project | null>(null);

  useEffect(() => {
    loadProjects();
  }, []);

  const loadProjects = async () => {
    try {
      const projectsData = await projectsApi.getAll();
      setProjects(projectsData);

      // Fetch tasks for each project
      const tasksMap: Record<number, Task[]> = {};
      await Promise.all(
        projectsData.map(async (project) => {
          const tasks = await tasksApi.getByProjectId(project.id);
          tasksMap[project.id] = tasks;
        })
      );
      setProjectTasks(tasksMap);
    } catch (error) {
      console.error('Error loading projects:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleProjectCreated = (newProject: Project) => {
    setProjects([newProject, ...projects]);
    setProjectTasks({ ...projectTasks, [newProject.id]: [] });
    setIsDialogOpen(false);
  };

  const handleEditClick = (e: React.MouseEvent, project: Project) => {
    e.preventDefault();
    setEditingProject(project);
  };

  const handleDeleteClick = (e: React.MouseEvent, project: Project) => {
    e.preventDefault();
    setDeletingProject(project);
  };

  const handleProjectUpdated = () => {
    loadProjects();
  };

  const handleProjectDeleted = () => {
    loadProjects();
  };

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <Skeleton className="h-8 w-48" />
          <Skeleton className="h-10 w-32" />
        </div>
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {[...Array(6)].map((_, i) => (
            <Card key={i}>
              <CardHeader>
                <Skeleton className="h-6 w-3/4" />
                <Skeleton className="h-4 w-full mt-2" />
              </CardHeader>
              <CardContent>
                <Skeleton className="h-2 w-full" />
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Projects</h1>
          <p className="text-muted-foreground mt-2">
            Manage and organize your projects
          </p>
        </div>
        <Button onClick={() => setIsDialogOpen(true)} className="gap-2">
          <Plus className="h-4 w-4" />
          Create Project
        </Button>
      </div>

      {/* Projects Grid */}
      {projects.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <FolderKanban className="h-12 w-12 text-muted-foreground mb-4" />
            <h3 className="text-lg font-medium mb-2">No projects yet</h3>
            <p className="text-sm text-muted-foreground mb-4">
              Get started by creating your first project
            </p>
            <Button onClick={() => setIsDialogOpen(true)} className="gap-2">
              <Plus className="h-4 w-4" />
              Create Project
            </Button>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {projects.map((project) => {
            const tasks = projectTasks[project.id] || [];
            const completedCount = tasks.filter((t) => t.isCompleted).length;
            const progress = tasks.length > 0
              ? Math.round((completedCount / tasks.length) * 100)
              : 0;

            return (
              <div key={project.id} className="relative group">
                <Link to={`/projects/${project.id}`}>
                  <Card className="hover:shadow-lg transition-shadow cursor-pointer h-full">
                    <CardHeader>
                      <CardTitle className="line-clamp-1 py-1 mb-2">{project.title}</CardTitle>
                      <CardDescription className="line-clamp-2">
                        {project.description}
                      </CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="space-y-2">
                        <div className="flex items-center justify-between text-sm">
                          <span className="text-muted-foreground">Progress</span>
                          <span className="font-medium">{progress}%</span>
                        </div>
                        <Progress value={progress} />
                        <div className="flex items-center justify-between text-xs text-muted-foreground mt-2">
                          <span>{completedCount}/{tasks.length} tasks</span>
                          <span>
                            {new Date(project.createdAt).toLocaleDateString()}
                          </span>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                </Link>
                <div className="absolute top-4 right-4 flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                  <Button
                    size="icon"
                    variant="secondary"
                    className="h-8 w-8"
                    onClick={(e) => handleEditClick(e, project)}
                  >
                    <Pencil className="h-4 w-4" />
                  </Button>
                  <Button
                    size="icon"
                    variant="destructive"
                    className="h-8 w-8"
                    onClick={(e) => handleDeleteClick(e, project)}
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Create Project Dialog */}
      <CreateProjectDialog
        open={isDialogOpen}
        onOpenChange={setIsDialogOpen}
        onProjectCreated={handleProjectCreated}
      />

      {/* Edit Project Dialog */}
      {editingProject && (
        <EditProjectDialog
          project={editingProject}
          open={!!editingProject}
          onOpenChange={(open) => !open && setEditingProject(null)}
          onSuccess={handleProjectUpdated}
        />
      )}

      {/* Delete Project Dialog */}
      {deletingProject && (
        <DeleteProjectDialog
          project={deletingProject}
          open={!!deletingProject}
          onOpenChange={(open) => !open && setDeletingProject(null)}
          onSuccess={handleProjectDeleted}
        />
      )}
    </div>
  );
}

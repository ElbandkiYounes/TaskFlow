import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { projectsApi, tasksApi } from '@/services/api';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { Progress } from '@/components/ui/progress';
import { Separator } from '@/components/ui/separator';
import { ArrowLeft, Plus, CalendarDays, Pencil, Trash2 } from 'lucide-react';
import type { Project, Task } from '@/types';
import TaskList from '@/components/tasks/TaskList';
import CreateTaskDialog from '@/components/tasks/CreateTaskDialog';
import { EditProjectDialog } from '@/components/projects/EditProjectDialog';
import { DeleteProjectDialog } from '@/components/projects/DeleteProjectDialog';

export default function ProjectDetails() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [project, setProject] = useState<Project | null>(null);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);

  useEffect(() => {
    if (id) {
      loadProjectData(parseInt(id));
    }
  }, [id]);

  const loadProjectData = async (projectId: number) => {
    try {
      const [projectData, tasksData] = await Promise.all([
        projectsApi.getById(projectId),
        tasksApi.getByProjectId(projectId),
      ]);
      setProject(projectData);
      setTasks(tasksData);
    } catch (error) {
      console.error('Error loading project:', error);
      navigate('/projects');
    } finally {
      setIsLoading(false);
    }
  };

  const handleTaskCreated = (newTask: Task) => {
    setTasks([...tasks, newTask]);
    setIsDialogOpen(false);
  };

  const handleTaskToggle = (updatedTask: Task) => {
    setTasks(tasks.map((t) => (t.id === updatedTask.id ? updatedTask : t)));
  };

  const handleTaskDelete = (taskId: number) => {
    setTasks(tasks.filter((t) => t.id !== taskId));
  };

  const handleTaskUpdate = () => {
    if (id) {
      loadProjectData(parseInt(id));
    }
  };

  const handleProjectUpdated = () => {
    if (id) {
      loadProjectData(parseInt(id));
    }
  };

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-10 w-32" />
        <Card>
          <CardHeader>
            <Skeleton className="h-8 w-3/4" />
            <Skeleton className="h-4 w-full mt-2" />
          </CardHeader>
          <CardContent>
            <Skeleton className="h-4 w-full" />
          </CardContent>
        </Card>
      </div>
    );
  }

  if (!project) {
    return null;
  }

  const completedCount = tasks.filter((t) => t.isCompleted).length;
  const progress = tasks.length > 0 ? Math.round((completedCount / tasks.length) * 100) : 0;

  return (
    <div className="space-y-6">
      {/* Back Button */}
      <Button variant="ghost" size="sm" onClick={() => navigate('/projects')} className="gap-2">
        <ArrowLeft className="h-4 w-4" />
        Back to Projects
      </Button>

      {/* Project Header */}
      <Card>
        <CardHeader>
          <div className="flex items-start justify-between">
            <div className="space-y-1">
              <CardTitle className="text-2xl">{project.title}</CardTitle>
              <CardDescription>{project.description}</CardDescription>
            </div>
            <div className="flex gap-2">
              <Button
                size="sm"
                variant="outline"
                onClick={() => setIsEditDialogOpen(true)}
                className="gap-2"
              >
                <Pencil className="h-4 w-4" />
                Edit
              </Button>
              <Button
                size="sm"
                variant="destructive"
                onClick={() => setIsDeleteDialogOpen(true)}
                className="gap-2"
              >
                <Trash2 className="h-4 w-4" />
                Delete
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <Separator />
          
          {/* Progress Section */}
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-medium">Project Progress</h3>
              <span className="text-2xl font-bold">{progress}%</span>
            </div>
            <Progress value={progress} className="h-2" />
            <div className="flex items-center justify-between text-sm text-muted-foreground">
              <span>{completedCount} of {tasks.length} tasks completed</span>
              <span
                className={
                  progress < 30
                    ? 'text-red-600 dark:text-red-400'
                    : progress < 70
                    ? 'text-yellow-600 dark:text-yellow-400'
                    : 'text-green-600 dark:text-green-400'
                }
              >
                {progress < 30 ? 'Just started' : progress < 70 ? 'In progress' : 'Almost done!'}
              </span>
            </div>
          </div>

          <Separator />

          {/* Project Info */}
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <p className="text-muted-foreground">Created</p>
              <p className="font-medium flex items-center gap-2 mt-1">
                <CalendarDays className="h-4 w-4" />
                {new Date(project.createdAt).toLocaleDateString('en-US', {
                  year: 'numeric',
                  month: 'long',
                  day: 'numeric',
                })}
              </p>
            </div>
            <div>
              <p className="text-muted-foreground">Last Updated</p>
              <p className="font-medium flex items-center gap-2 mt-1">
                <CalendarDays className="h-4 w-4" />
                {new Date(project.updatedAt).toLocaleDateString('en-US', {
                  year: 'numeric',
                  month: 'long',
                  day: 'numeric',
                })}
              </p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Tasks Section */}
      <div>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-2xl font-semibold tracking-tight">Tasks</h2>
          <Button onClick={() => setIsDialogOpen(true)} className="gap-2">
            <Plus className="h-4 w-4" />
            Add Task
          </Button>
        </div>

        <TaskList
          tasks={tasks}
          onTaskToggle={handleTaskToggle}
          onTaskDelete={handleTaskDelete}
          onTaskUpdate={handleTaskUpdate}
        />
      </div>

      {/* Create Task Dialog */}
      <CreateTaskDialog
        open={isDialogOpen}
        onOpenChange={setIsDialogOpen}
        projectId={project.id}
        onTaskCreated={handleTaskCreated}
      />

      {/* Edit Project Dialog */}
      {project && (
        <EditProjectDialog
          project={project}
          open={isEditDialogOpen}
          onOpenChange={setIsEditDialogOpen}
          onSuccess={handleProjectUpdated}
        />
      )}

      {/* Delete Project Dialog */}
      {project && (
        <DeleteProjectDialog
          project={project}
          open={isDeleteDialogOpen}
          onOpenChange={setIsDeleteDialogOpen}
          redirectAfterDelete={true}
        />
      )}
    </div>
  );
}

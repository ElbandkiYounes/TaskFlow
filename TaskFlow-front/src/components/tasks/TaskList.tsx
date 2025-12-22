import { useState } from 'react';
import { tasksApi } from '@/services/api';
import { Card, CardContent } from '@/components/ui/card';
import { Checkbox } from '@/components/ui/checkbox';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Calendar, Trash2, CheckCircle2, Circle, Pencil } from 'lucide-react';
import { toast } from 'sonner';
import type { Task } from '@/types';
import { formatDistanceToNow, isPast, isToday } from 'date-fns';
import DeleteTaskDialog from './DeleteTaskDialog';
import { EditTaskDialog } from './EditTaskDialog';

interface TaskListProps {
  tasks: Task[];
  onTaskToggle: (task: Task) => void;
  onTaskDelete: (taskId: number) => void;
  onTaskUpdate?: () => void;
}

export default function TaskList({ tasks, onTaskToggle, onTaskDelete, onTaskUpdate }: TaskListProps) {
  const [taskToDelete, setTaskToDelete] = useState<Task | null>(null);
  const [taskToEdit, setTaskToEdit] = useState<Task | null>(null);
  const [togglingTaskId, setTogglingTaskId] = useState<number | null>(null);

  const handleToggle = async (task: Task) => {
    setTogglingTaskId(task.id);
    try {
      const updatedTask = await tasksApi.toggleComplete(task.id);
      onTaskToggle(updatedTask);
      toast.success(updatedTask.isCompleted ? 'Task completed!' : 'Task marked as incomplete');
    } catch (error) {
      toast.error('Failed to update task');
    } finally {
      setTogglingTaskId(null);
    }
  };

  const getDueDateBadge = (dueDate: string | null) => {
    if (!dueDate) return null;

    const date = new Date(dueDate);
    const isOverdue = isPast(date) && !isToday(date);
    const isDueToday = isToday(date);

    if (isOverdue) {
      return (
        <Badge variant="destructive" className="gap-1">
          <Calendar className="h-3 w-3" />
          Overdue
        </Badge>
      );
    }

    if (isDueToday) {
      return (
        <Badge className="gap-1 bg-yellow-600 hover:bg-yellow-700">
          <Calendar className="h-3 w-3" />
          Due today
        </Badge>
      );
    }

    return (
      <Badge variant="secondary" className="gap-1">
        <Calendar className="h-3 w-3" />
        {formatDistanceToNow(date, { addSuffix: true })}
      </Badge>
    );
  };

  if (tasks.length === 0) {
    return (
      <Card>
        <CardContent className="flex flex-col items-center justify-center py-12">
          <Circle className="h-12 w-12 text-muted-foreground mb-4" />
          <h3 className="text-lg font-medium mb-2">No tasks yet</h3>
          <p className="text-sm text-muted-foreground">
            Add your first task to get started
          </p>
        </CardContent>
      </Card>
    );
  }

  const pendingTasks = tasks.filter((t) => !t.isCompleted);
  const completedTasks = tasks.filter((t) => t.isCompleted);

  return (
    <div className="space-y-4">
      {/* Pending Tasks */}
      {pendingTasks.length > 0 && (
        <div className="space-y-2">
          <h3 className="text-sm font-medium text-muted-foreground px-1">
            Pending ({pendingTasks.length})
          </h3>
          {pendingTasks.map((task) => (
            <Card
              key={task.id}
              className="hover:bg-muted/50 transition-colors cursor-pointer"
            >
              <CardContent className="p-4">
                <div className="flex items-start gap-3">
                  <Checkbox
                    checked={task.isCompleted}
                    onCheckedChange={() => handleToggle(task)}
                    disabled={togglingTaskId === task.id}
                    className="mt-1"
                  />
                  <div className="flex-1 space-y-1">
                    <div className="flex items-start justify-between gap-2">
                      <h4 className="font-medium leading-none">{task.title}</h4>
                      <div className="flex gap-1">
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-8 w-8 text-muted-foreground hover:text-primary"
                          onClick={() => setTaskToEdit(task)}
                        >
                          <Pencil className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-8 w-8 text-muted-foreground hover:text-destructive"
                          onClick={() => setTaskToDelete(task)}
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                    {task.description && (
                      <p className="text-sm text-muted-foreground">{task.description}</p>
                    )}
                    {task.dueDate && (
                      <div className="pt-1">{getDueDateBadge(task.dueDate)}</div>
                    )}
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      {/* Completed Tasks */}
      {completedTasks.length > 0 && (
        <div className="space-y-2">
          <h3 className="text-sm font-medium text-muted-foreground px-1">
            Completed ({completedTasks.length})
          </h3>
          {completedTasks.map((task) => (
            <Card
              key={task.id}
              className="hover:bg-muted/50 transition-colors cursor-pointer opacity-75"
            >
              <CardContent className="p-4">
                <div className="flex items-start gap-3">
                  <Checkbox
                    checked={task.isCompleted}
                    onCheckedChange={() => handleToggle(task)}
                    disabled={togglingTaskId === task.id}
                    className="mt-1"
                  />
                  <div className="flex-1 space-y-1">
                    <div className="flex items-start justify-between gap-2">
                      <h4 className="font-medium leading-none line-through">{task.title}</h4>
                      <div className="flex gap-1">
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-8 w-8 text-muted-foreground hover:text-primary"
                          onClick={() => setTaskToEdit(task)}
                        >
                          <Pencil className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-8 w-8 text-muted-foreground hover:text-destructive"
                          onClick={() => setTaskToDelete(task)}
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                    {task.description && (
                      <p className="text-sm text-muted-foreground line-through">
                        {task.description}
                      </p>
                    )}
                    <div className="flex items-center gap-2 pt-1">
                      <Badge variant="secondary" className="gap-1">
                        <CheckCircle2 className="h-3 w-3" />
                        Completed
                      </Badge>
                      {task.dueDate && getDueDateBadge(task.dueDate)}
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      {/* Delete Confirmation Dialog */}
      {taskToDelete && (
        <DeleteTaskDialog
          task={taskToDelete}
          onClose={() => setTaskToDelete(null)}
          onDelete={onTaskDelete}
        />
      )}

      {/* Edit Task Dialog */}
      {taskToEdit && (
        <EditTaskDialog
          task={taskToEdit}
          open={!!taskToEdit}
          onOpenChange={(open) => !open && setTaskToEdit(null)}
          onSuccess={() => {
            setTaskToEdit(null);
            if (onTaskUpdate) {
              onTaskUpdate();
            }
          }}
        />
      )}
    </div>
  );
}

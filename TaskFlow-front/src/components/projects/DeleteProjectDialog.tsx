import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { AlertTriangle, Loader2 } from 'lucide-react';
import { projectsApi } from '@/services/api';
import type { Project } from '@/types';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';

interface DeleteProjectDialogProps {
  project: Project;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess?: () => void;
  redirectAfterDelete?: boolean;
}

export function DeleteProjectDialog({
  project,
  open,
  onOpenChange,
  onSuccess,
  redirectAfterDelete = false,
}: DeleteProjectDialogProps) {
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleDelete = async () => {
    setIsLoading(true);
    try {
      await projectsApi.delete(project.id);
      toast.success('Project deleted successfully');
      onOpenChange(false);
      
      if (redirectAfterDelete) {
        navigate('/projects');
      } else if (onSuccess) {
        onSuccess();
      }
    } catch (error) {
      toast.error('Failed to delete project');
      console.error('Error deleting project:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <AlertTriangle className="h-5 w-5 text-red-500" />
            Delete Project
          </DialogTitle>
          <DialogDescription>
            Are you sure you want to delete this project? This action cannot be
            undone and will also delete all associated tasks.
          </DialogDescription>
        </DialogHeader>
        <div className="rounded-lg border border-red-200 bg-red-50 p-4">
          <p className="font-semibold text-red-900">{project.title}</p>
          {project.description && (
            <p className="mt-1 text-sm text-red-700">{project.description}</p>
          )}
        </div>
        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => onOpenChange(false)}
            disabled={isLoading}
          >
            Cancel
          </Button>
          <Button
            variant="destructive"
            onClick={handleDelete}
            disabled={isLoading}
          >
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Deleting...
              </>
            ) : (
              'Delete Project'
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

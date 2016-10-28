package wrappers

import (
	"errors"

	"github.com/aws/amazon-ecs-event-stream-handler/internal/client"
	"github.com/aws/amazon-ecs-event-stream-handler/internal/client/operations"
	"github.com/aws/amazon-ecs-event-stream-handler/internal/models"
)

const (
	getTaskNotFoundException     = "GetTaskNotFound"
	getInstanceNotFoundException = "GetInstanceNotFound"
)

type ESHWrapper struct {
	client *client.AmazonEcsEsh
}

func NewESHWrapper() ESHWrapper {
	return ESHWrapper{
		client: client.NewHTTPClient(nil),
	}
}

func (eshWrapper ESHWrapper) GetTask(taskARN string) (*models.TaskModel, error) {
	in := operations.NewGetTaskParams()
	in.SetArn(taskARN)
	resp, err := eshWrapper.client.Operations.GetTask(in)
	if err != nil {
		return nil, err
	}
	task := resp.Payload
	return task, nil
}

func (eshWrapper ESHWrapper) TryGetTask(taskARN string) (string, error) {
	in := operations.NewGetTaskParams()
	in.SetArn(taskARN)
	_, err := eshWrapper.client.Operations.GetTask(in)
	if err != nil {
		if _, ok := err.(*operations.GetTaskNotFound); ok {
			return getTaskNotFoundException, nil
		} else {
			return "", errors.New("Unknown exception when calling Get Task")
		}
	}
	return "", errors.New("Expected an exception when calling Get Task, but none received")
}

func (eshWrapper ESHWrapper) ListTasks() ([]*models.TaskModel, error) {
	in := operations.NewListTasksParams()
	resp, err := eshWrapper.client.Operations.ListTasks(in)
	if err != nil {
		return nil, err
	}
	tasks := resp.Payload
	return tasks, nil
}

func (eshWrapper ESHWrapper) FilterTasksByStatus(status string) ([]*models.TaskModel, error) {
	in := operations.NewFilterTasksParams()
	in.SetStatus(status)
	resp, err := eshWrapper.client.Operations.FilterTasks(in)
	if err != nil {
		return nil, err
	}
	tasks := resp.Payload
	return tasks, nil
}

func (eshWrapper ESHWrapper) GetInstance(instanceARN string) (*models.ContainerInstanceModel, error) {
	in := operations.NewGetInstanceParams()
	in.SetArn(instanceARN)
	resp, err := eshWrapper.client.Operations.GetInstance(in)
	if err != nil {
		return nil, err
	}
	instance := resp.Payload
	return instance, nil
}

func (eshWrapper ESHWrapper) TryGetInstance(instanceARN string) (string, error) {
	in := operations.NewGetInstanceParams()
	in.SetArn(instanceARN)
	_, err := eshWrapper.client.Operations.GetInstance(in)
	if err != nil {
		if _, ok := err.(*operations.GetInstanceNotFound); ok {
			return getInstanceNotFoundException, nil
		} else {
			return "", errors.New("Unknown exception when calling Get Instance")
		}
	}
	return "", errors.New("Expected an exception when calling Get Instance, but none received")
}

func (eshWrapper ESHWrapper) ListInstances() ([]*models.ContainerInstanceModel, error) {
	in := operations.NewListInstancesParams()
	resp, err := eshWrapper.client.Operations.ListInstances(in)
	if err != nil {
		return nil, err
	}
	instances := resp.Payload
	return instances, nil
}

func (eshWrapper ESHWrapper) FilterInstancesByClusterName(clusterName string) ([]*models.ContainerInstanceModel, error) {
	in := operations.NewFilterInstancesParams()
	in.SetCluster(clusterName)
	resp, err := eshWrapper.client.Operations.FilterInstances(in)
	if err != nil {
		return nil, err
	}
	instances := resp.Payload
	return instances, nil
}
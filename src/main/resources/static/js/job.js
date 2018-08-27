function runJob(jobName) {
    var api = apiUrl + "/jobs/fire/" + jobName;

    $.ajax({
        url: api,
        type: "put",
        success: function (data) {
            if (data.code != 200) {
                alert("执行任务:" + jobName + "失败," + data.message)
            } else {
                alert("执行任务:" + jobName + "成功")
                window.location.reload()
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var status = XMLHttpRequest.status;
        }
    });
}

function pauseJob(jobName) {
    var api = apiUrl + "/jobs/pause/" + jobName;

    $.ajax({
        url: api,
        type: "post",
        success: function (data) {
            if (data.code != 200) {
                alert("暂停任务:" + jobName + "失败")
            } else {
                alert("暂停任务:" + jobName + "成功")
                window.location.reload()
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var status = XMLHttpRequest.status;
        }
    });

}

function resumeJob(jobName) {
    var api = apiUrl + "/jobs/resume/" + jobName;

    $.ajax({
        url: api,
        type: "post",
        success: function (data) {
            if (data.code != 200) {
                alert("重启任务:" + jobName + "失败")
            } else {
                alert("重启任务:" + jobName + "成功")
                window.location.reload()
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var status = XMLHttpRequest.status;
        }
    });

}

function initOption() {
    var api = apiUrl + "/classes";

    $.ajax({
        url: api,
        type: "get",
        success: function (data) {
            if (data.code != 200) {

            } else {
                $("#creatClassName").empty();
                var html = "";
                var classList = data.data;
                for (var i = 0; i < classList.length; i++) {
                    html = html + '<option value="' + classList[i].fullName + '">' + classList[i].simpleName + '</option>';
                }
                $('#creatClassName').append(html);
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var status = XMLHttpRequest.status;
        }
    });
}

function createJob() {
    var api = apiUrl + "/jobs";
    var jobClassName = $('#creatClassName').val();
    var jobName = $('#createJobName').val();
    var jobGroup = $('#createJobGroup').val();
    var triggerName = $('#createTrigger').val();
    var triggerGroup = $('#createTriggerGroup').val();
    var cronExpression = $('#createCronExpression').val();
    var param = $('#createParam').val();

    $.ajax({
        url: api,
        data: {
            "className": jobClassName,
            "jobName": jobName,
            "jobGroupName": jobGroup,
            "triggerName": triggerName,
            "triggerGroupName": triggerGroup,
            "cron": cronExpression,
            "description": param,
            "param": param
        },
        type: "post",
        success: function (data) {
            if (data.code != 200) {
                alert("任务：" + jobName + "创建失败，原因：" + data.message);
            } else {
                alert("任务：" + jobName + "创建成功")
                window.location.reload()
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var status = XMLHttpRequest.status;
        }
    });
}

function updateJob() {
    var jobName = $('#updateJobName').val();
    var api = apiUrl + "/jobs/" + jobName;
    var cronExpression = $('#updateCronExpression').val();
    $.ajax({
        url: api,
        data: {
            "cron": cronExpression
        },
        type: "post",
        success: function (data) {
            if (data.code != 200) {
                alert("任务：" + jobName + "更新失败，原因：" + data.message);
            } else {
                alert("任务：" + jobName + "更新成功")
                window.location.reload()
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var status = XMLHttpRequest.status;
        }
    });
}

function deleteJob() {
    var jobName = $('#deleteJobName').val();
    var api = apiUrl + "/jobs/" + jobName;
    $.ajax({
        url: api,
        type: "delete",
        success: function (data) {
            if (data.code != 200) {
                alert("任务：" + jobName + "删除失败，原因：" + data.message);
            } else {
                alert("任务：" + jobName + "删除成功")
                window.location.reload()
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var status = XMLHttpRequest.status;
        }
    });
}

function initTiggerGroups() {
    var api = apiUrl + "/triggers";

    $.ajax({
        url: api,
        type: "get",
        success: function (data) {
            if (data.code != 200) {

            } else {
                $("#createJobGroup").empty();
                var html = "";
                var groupList = data.data;
                for (var i = 0; i < groupList.length; i++) {
                    html = html + '<option value="' + groupList[i] + '">' + groupList[i] + '</option>';
                }
                $('#createJobGroup').append(html);
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var status = XMLHttpRequest.status;
        }
    });
}


//初始化删除modal
function createdeleteJob(jobName) {
    var api = apiUrl + "/jobs/" + jobName;
    $.ajax({
        url: api,
        type: "get",
        success: function (data) {
            if (data.code != 200) {

            } else {
                var job = data.data;
                $('#deleteJobName').val(job.jobName);
                $('#deleteJobExpresion').val(job.cronExpression);
                $('#modal-delete').modal();
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var status = XMLHttpRequest.status;
        }
    });
}

//初始化编辑modal
function editJob(jobName) {
    var api = apiUrl + "/jobs/" + jobName;
    $.ajax({
        url: api,
        type: "get",
        success: function (data) {
            if (data.code != 200) {

            } else {
                var job = data.data;
                $('#updateJobName').val(job.jobName);
                $('#modal-update').modal();
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var status = XMLHttpRequest.status;
        }
    });
}


//重写toLocalString,格式化毫秒时间戳为年月日时分秒格式
Date.prototype.toLocaleString = function () {
    return this.getFullYear() + "年" + (this.getMonth() + 1) + "月" + this.getDate() + "日 " + this.getHours() + "点" + this.getMinutes() + "分" + this.getSeconds() + "秒";
};

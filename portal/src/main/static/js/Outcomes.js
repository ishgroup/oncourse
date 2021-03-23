import jQuery, * as $ from "./jquery-1.11.2";


var $j = jQuery.noConflict();

var OutcomeSouce = {
    ENROLMENT_SOURCE: 'Enrolment',
    MODULE_SOURCE: 'Module'
}

var outcomesContent;

function scrollTop() {
    $j('html, body').animate({scrollTop: $j("#wrap").offset().top}, 500);
}

$j(document).ready(function () {

    if ($j('div#class-outcomes')) {
        outcomesContent = $j('div#class-outcomes > div.tab-content');

        $j('li[id^="outcomeModuleId_"]').on('click', function(){
            renderModuleOutcomes($j(this).attr('id').replace('outcomeModuleId_',''), window.location.pathname.match("\\d+$")[0]);
            scrollTop();
        });
        $j('li[id^="outcomeEnrolmentId_"]').on('click', function(){
            renderEnrolmentOutcomes($j(this).attr('id').replace('outcomeEnrolmentId_',''));
            scrollTop();
        });
        $j('a[href="#mark-by-outcome"]').on('click', function(){
            $j('#module-outcome-content').remove();
            $j('#enrolment-outcome-content').remove();
        });
        $j('a[href="#mark-by-student"]').on('click', function(){
            $j('#module-outcome-content').remove();
            $j('#enrolment-outcome-content').remove();
        });

    }
});

function renderModuleOutcomes(moduleId, courseClassId) {
    $j('#mark-by-outcome').removeClass('active');
    $j('#module-outcome-content').remove();
    var self = this;
    $j.ajax({
        cache: false,
        async: false,
        dataType: "json",
        url: '/portal/class.outcomes:getModuleOutcomes/' + courseClassId + '/' + moduleId,
        type: 'GET',
        success: function (data) {
            if (data.content) {
                outcomesContent.append(data.content)
                $j('a#back-to-modules').on('click', function(){
                    $j('#module-outcome-content').remove();
                    $j('#mark-by-outcome').addClass('active');
                });
                $j('div[id^="mark-outcomeId-"]').each(function(){
                    new OutcomeCtrl().init($j(this), OutcomeSouce.MODULE_SOURCE, moduleId);
                })
                new ModuleCtrl().init($j('div[id^="mark-moduleId-"]'));

            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            window.location.reload();
            console.log("Unexpected exception");
        }
    });
}


function renderEnrolmentOutcomes(enrolmentId) {
    $j('#mark-by-student').removeClass('active');
    $j('#enrolment-outcome-content').remove();
    $j.ajax({
        cache: false,
        async: false,
        dataType: "json",
        url: '/portal/class.outcomes:getEnrolmentOutcomes/'+ enrolmentId,
        type: 'GET',
        success: function (data) {
            if (data.content) {
                outcomesContent.append(data.content)
                $j('a#back-to-students').on('click', function(){
                    $j('#enrolment-outcome-content').remove();
                    $j('#mark-by-student').addClass('active')
                });
                $j('div[id^="mark-outcomeId-"]').each(function(){
                    new OutcomeCtrl().init($j(this), OutcomeSouce.ENROLMENT_SOURCE, enrolmentId);
                });
                new EnrolmentCtrl().init($j('div[id^="mark-enrolmentId-"]'));

            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            window.location.reload();
            console.log("Unexpected exception");
        }
    });
}

function refreshSelection(selected) {
    $j(selected).closest('.nav-menu').find('li > button').each(function(){
        $j(this).removeClass('ui-priority-primary')
    });
    $j(selected).addClass('ui-priority-primary');
}


var EnrolmentCtrl = function () {
};


EnrolmentCtrl.prototype = {
    status: null,
    enrolment: null,
    enrolmentId: null,

    init: function (enrolment) {
        this.enrolment = enrolment;
        this.enrolmentId = $j(enrolment).attr('id').replace('mark-enrolmentId-', '');

        var self = this;
        $j(this.enrolment).children('ul').children('li').children('button').on('click', function () {
            refreshSelection(this);
            self.status = $j(this).attr('data-status');
        });
        $j(this.enrolment).children('button').on('click', function () {
            if (self.status == null) {
                alert('You must choose status.');
            } else {
                var setDate = $j(self.enrolment).find('.set-end-date').is(":checked");
                $j.ajax({
                    cache: false,
                    async: false,
                    dataType: "json",
                    url: '/portal/class.outcomes:saveEnrolment/' + self.enrolmentId + '/' + self.status + '/' + setDate,
                    type: 'GET',
                    success: function () {
                        renderEnrolmentOutcomes(self.enrolmentId);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        window.location.reload();
                        console.log("Unexpected exception");
                    }
                });
            }
        });
    }
};


var ModuleCtrl = function () {
};


ModuleCtrl.prototype = {
    status: null,
    module: null,
    moduleId: null,
    classId: null,

    init: function (module) {
        this.module = module;
        this.moduleId = $j(module).attr('id').replace('mark-moduleId-', '');
        this.classId = $j(module).attr('data-classId');

        var self = this;
        $j(this.module).children('ul').children('li').children('button').on('click', function () {
            refreshSelection(this);
            self.status = $j(this).attr('data-status');
        });
        $j(this.module).children('button').on('click', function () {
            if (self.status == null) {
                alert('You must choose status.');
            } else {
                var setDate = $j(self.module).find('.set-end-date').is(":checked");
                $j.ajax({
                    cache: false,
                    async: false,
                    dataType: "json",
                    url: '/portal/class.outcomes:saveModule/' + self.moduleId + '/'+ self.classId + '/' + self.status + '/' + setDate,
                    type: 'GET',
                    success: function () {
                        renderModuleOutcomes(self.moduleId, window.location.pathname.match("\\d+$")[0]);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        window.location.reload();
                        console.log("Unexpected exception");
                    }
                });
            }
        });
    }
};



var OutcomeCtrl = function () {
};

OutcomeCtrl.prototype = {

    status: null,
    outcome: null,
    source: null,
    sourceId: null,
    outcomeId: null,

    init: function (outcome, source, sourceId) {
        this.outcome = outcome;
        this.source = source;
        this.sourceId = sourceId;
        this.outcomeId = $j(outcome).attr('id').replace('mark-outcomeId-', '');

        var self = this;
        $j(this.outcome).children('ul').children('li').children('button').on('click', function () {
            refreshSelection(this);
            self.status = $j(this).attr('data-status');
        });
        $j(this.outcome).children('button').on('click', function () {
            if (self.status == null) {
                alert('You must choose status.');
            } else {
                var setDate = $j(self.outcome).find('.set-end-date').is(":checked");

                $j.ajax({
                    cache: false,
                    async: false,
                    dataType: "json",
                    url: '/portal/class.outcomes:saveOutcome/' + self.outcomeId + '/' + self.status + '/' + setDate,
                    type: 'GET',
                    success: function () {
                        self.source == OutcomeSouce.ENROLMENT_SOURCE ? renderEnrolmentOutcomes(self.sourceId) : renderModuleOutcomes(self.sourceId, window.location.pathname.match("\\d+$")[0]);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        window.location.reload();
                        console.log("Unexpected exception");
                    }
                });
            }
        });
    }
};

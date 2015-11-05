'use strict';

(function() {

  function WelcomeController(UserService, $location, $window, $scope) {
    var self = this;

    self.userService = UserService;

    $scope.$on('forbiddenRequest', function(event, error) {
      self.error = error;
    });

    $scope.$on('unauthorizedRequest', function() {
      self.unauthorized = {
        link: 'http://localhost:8081/oauth/authorize?client_id=read-only-client&response_type=token&redirect_uri=' + $location.absUrl()
      }
    });

    this.gotoProviderLogin = function() {
      $window.location = this.unauthorized.link;
    }
  }

  function UserService($rootScope) {
    var self = this;
    $rootScope.$on('loggedIn', function(event, loggedInUser) {
      self.user = loggedInUser;
    });
  }

  var app = angular.module('app', [])
    .controller('WelcomeController', WelcomeController)
    .service('UserService', UserService)
    .config(function($locationProvider, $httpProvider) {
      $locationProvider.html5Mode({enabled: true, requireBase: false}).hashPrefix('#!');
      var token = localStorage.getItem('oauthToken');
      if (token) $httpProvider.defaults.headers.common.Authorization = 'Bearer ' + token;
      $httpProvider.interceptors.push(function($q, $rootScope) {
        return {
          responseError: function(response) {
            if (response.status === 403) $rootScope.$broadcast('forbiddenRequest', response.data);
            if (response.status === 401) $rootScope.$broadcast('unauthorizedRequest', response.data);
            return response;
          }
        }
      });
    })
    .run(function($location, $http) {
      var hash = $location.hash();
      if (hash.indexOf('access_token') > -1) {
        var extractRegex = /access_token=([a-zA-Z0-9-]*)&/;
        var result = extractRegex.exec(hash);
        if (result.length > 1) {
          localStorage.setItem('oauthToken', result[1]);
          $http.defaults.headers.common.Authorization = 'Bearer ' + result[1];
        }
      }
      $location.hash('');
    })
    .run(function($http, $rootScope) {
      $http.get('http://localhost:8080/me').then(function(response) {
        $rootScope.$broadcast('loggedIn', response.data);
      });
    });
})();


//http://localhost:9090/#/access_token=6edae335-331b-4be9-9a6f-9814d60b2980&token_type=bearer&expires_in=7199&scope=read
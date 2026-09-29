"""
URL configuration for y_app project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/6.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""

from django.contrib import admin
from django.urls import include, path
from rest_framework.routers import DefaultRouter
from rest_framework_simplejwt.views import (
    TokenObtainPairView,
    TokenRefreshView,
)

from profiles.views import MeView, PostViewSet, RegisterView, FeedView

router = DefaultRouter()
router.register("posts", PostViewSet, basename="posts")

urlpatterns = [
    path("api/", include(router.urls)),
    path("api/me/", MeView.as_view(), name="me"),
    path("api/feed/", FeedView.as_view(), name="feed"),
    path("api/auth/register/", RegisterView.as_view(), name="register"),
    path("api/auth/token/", TokenObtainPairView.as_view()),
    path("api/auth/token/refresh/", TokenRefreshView.as_view()),
    # path("api-auth/", include("rest_framework.urls", namespace="rest_framework")),
    # path("user/", include("django.contrib.auth.urls")), # https://docs.djangoproject.com/en/6.1/topics/auth/default/#module-django.contrib.auth.views
]

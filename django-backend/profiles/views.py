from rest_framework.viewsets import ModelViewSet

from .models import Profile, Post
from .serializers import ProfileSerializer, PostSerializer

class ProfileViewSet(ModelViewSet):
    queryset = Profile.objects.all()
    serializer_class = ProfileSerializer

class PostViewSet(ModelViewSet):
    queryset = Post.objects.all()
    serializer_class = PostSerializer
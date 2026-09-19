from django.test import TestCase, Client
from django.contrib.auth.models import User

from .models import Profile

class ProfilesViewsTestCase(TestCase):
    def setUp(self):
        user = User.objects.create(username="Aldo", password="password")
        Profile.objects.create(user=user)
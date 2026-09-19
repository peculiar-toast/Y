from django.core.management.base import BaseCommand
from django.contrib.auth.models import User
from faker import Faker
import random

from profiles.models import Profile, Post

class Command(BaseCommand):
    help = "Seed DB with fake data"

    def add_arguments(self, parser):
        parser.add_argument(
            "--count",
            type=int,
            default=10,
        )

    def handle(self, *args, **options):
        fake = Faker()

        for _ in range(10):
            user = User.objects.create_user(
                username=fake.user_name(),
                password="password",
            )

            Profile.objects.create(
                user=user,
            )

            for _ in range(random.randint(1, 5)):
                Post.objects.create(
                    user=user,
                    title=f"{fake.cryptocurrency_name()} is { "Good" if random.randint(0,1) % 2 == 0 else "Bad" }"
                )

        self.stdout.write(
            self.style.SUCCESS("Database seeded successfully!")
        )
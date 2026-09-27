from django.core.management.base import BaseCommand
from django.contrib.auth.models import User
from django.core.files import File
from django.conf import settings
from django.db import transaction
from faker import Faker
from pathlib import Path
import random
import os

from profiles.models import Profile, Post

class Command(BaseCommand):
    help = "Seed DB with fake data"

    SEED_PREFIX = "seed_user_"
    SEED_VALUE = 12345

    def add_arguments(self, parser):
        parser.add_argument(
            "--count",
            type=int,
            default=10,
        )
        parser.add_argument(    
            "--no-clean",
            action="store_true",
            help="Do not delete existing seeded data"
        )

    def handle(self, *args, **options):
        count = options["count"]
        fake = Faker()
        fake.seed_instance(self.SEED_VALUE)
        random.seed(self.SEED_VALUE)

        audio_path = Path(settings.MEDIA_ROOT) / "audio"

        if not audio_path.exists():
            self.stdout.write(
                self.style.ERROR(f"Audio directory does not exist: {audio_path}")
            )
            return
        
        audio_files = [
            f.resolve()
            for f in audio_path.iterdir()
            if f.is_file()
        ]

        if not options["no_clean"]:
            self.cleanup()

        with transaction.atomic():
            for index in range(options["count"]):
                username = f"{self.SEED_PREFIX}{index}"
                
                user = User.objects.create_user(
                    username=username,
                    password="password",
                )
                
                Profile.objects.create(
                    user=user,
                )
                
                for _ in range(random.randint(0, 5)):
                    file = random.choice(audio_files)
                    
                    with open(file.absolute().resolve(), "rb") as f:
                        
                        Post.objects.create(
                            user=user,
                            title=f"{fake.cryptocurrency_name()} is { random.choice(['Good', 'Bad'])}",
                            audio=File(f, name=f"{user}-{file.name}")
                        )
                        
            self.stdout.write(
                self.style.SUCCESS("Database seeded successfully!")
            )

    def cleanup(self):
        users = User.objects.filter(
            username__startswith=self.SEED_PREFIX
        )

        posts = Post.objects.filter(user__in=users)

        with transaction.atomic():
            for post in posts:
                if post.audio:
                    post.audio.delete(save=False)

            posts.delete()
            Profile.objects.filter(user__in=users).delete()
            users.delete()

            self.stdout.write("Removed existing seed data.")

from django.db import models
from django.conf import settings
from django.urls import reverse

class Profile(models.Model):

    user = models.OneToOneField(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name="profile"
    )

    avatar = models.ImageField(
        upload_to="avatars/",
        null=True,
        blank=True
    )

    date_created = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return "Profile %s" % self.user.username[:10]

    def get_absolute_url(self):
        return reverse("Profile_detail", kwargs={"pk": self.pk})

class Post(models.Model):

    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name="posts"
    )

    title = models.CharField(max_length=50)

    audio = models.FileField(upload_to="audio/")

    date_created = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = "Post"
        verbose_name_plural = "Posts"

    def __str__(self):
        return self.title

    def get_absolute_url(self):
        return reverse("Post_detail", kwargs={"pk": self.pk})

class Comment(models.Model):

    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name="comments"
    )

    content = models.TextField()
    date_created = models.DateTimeField(auto_now_add=True)

    def get_absolute_url(self):
        return reverse("Comment_detail", kwargs={"pk": self.pk})
    
    def __str__(self):
        return f"{self.user}: {self.content[:25]}"

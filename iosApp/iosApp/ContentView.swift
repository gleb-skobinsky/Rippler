import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ZStack {
            ComposeView()
            Button { } label: {
                Text("SwiftUI Button")
                    .foregroundColor(.black)
                    .frame(maxWidth: .infinity)
                    .frame(height: 48)
                    .background(
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color.lightGray)
                    )
                    .padding(.horizontal, 32)
            }
        }
        .ignoresSafeArea(.all)
    }
}

extension Color {
    public static var lightGray: Color {
        return Color(white: 0.800)
    }
}


